package com.nttdata.bc46account.service;

import com.nttdata.bc46account.exceptions.AccountNotFoundException;
import com.nttdata.bc46account.exceptions.DuplicateAccountException;
import com.nttdata.bc46account.exceptions.InvalidAccountTypeException;
import com.nttdata.bc46account.model.Account;
import com.nttdata.bc46account.model.Movement;
import com.nttdata.bc46account.model.OperationType;
import com.nttdata.bc46account.model.Persona;
import com.nttdata.bc46account.repository.AccountRepository;
import com.nttdata.bc46account.repository.MovementRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Ntt Data - Top Employer 2023.
 * Todos los derechos Reservados.
 */
@Service
public class AccountImpl implements AccountService {
  @Autowired
  AccountRepository accountRepository;

  @Autowired
  MovementRepository movementRepository;

  @Override
  public Flux<Account> findAll() {
    return accountRepository.findAll()
        .map(accountAll -> {
          accountAll.getBankMovements().sort(Comparator.comparing(Movement::getCreationDatetime).reversed());
          return accountAll;
        });
  }

  @Override
  public Flux<Account> findByIdCustomer(String idCustomer) {
    return accountRepository.findByIdCustomer(idCustomer)
        .map(accountByCustomer -> {
          accountByCustomer.getBankMovements().sort(Comparator.comparing(Movement::getCreationDatetime).reversed());
          return accountByCustomer;
        });
  }

  @Override
  public Mono<Account> findById(String id) {
    return accountRepository.findById(id)
        .map(account -> {
          /**Ordena las operaciones de fecha por creación de hora más reciente a más antigua.*/
          account.getBankMovements().sort(Comparator.comparing(Movement::getCreationDatetime).reversed());
          return account;
        });
  }

  @Override
  public Mono<Account> save(Account account) {
    String idProduct = account.getIdProduct();
    String accountType = account.getAccountType();


    if ("empresarial".equals(accountType)) {
      List<Persona> holders = account.getHolderAccount();
      List<Persona> authorizedSigners = account.getAuthorizedSigner();

      /**Verificar la cantidad de titulares; debe ser al menos 1.*/
      if (holders == null || holders.isEmpty()) {
        return Mono.error(new InvalidAccountTypeException("Debe haber al menos un titular para" +
            " una cuenta empresarial"));
      }

      /**Verificar la cantidad de firmantes autorizados (máximo 4).*/
      if (authorizedSigners != null && authorizedSigners.size() > 4) {
        return Mono.error(new InvalidAccountTypeException("No se permiten más de 4 firmantes" +
            " autorizados para una cuenta empresarial"));
      }

      if ("P001".equals(idProduct) || "P003".equals(idProduct)) {
        return Mono.error(() ->
            new InvalidAccountTypeException("Un cliente empresarial no puede tener una " +
                "cuenta de ahorro o de plazo fijo."));
      }
    } else {
      /** Verifica si el cliente ya tiene una cuenta del mismo tipo. */
      return hasAccountOfType(account.getIdCustomer(), idProduct)
          .flatMap(accountExists -> {
            if (accountExists) {
              /** Si Cliente ya tiene una cuenta del mismo tipo. */
              return Mono.error(() ->
                  new DuplicateAccountException("Un cliente personal solo puede tener un máximo de " +
                      "una cuenta de ahorro, una cuenta corriente o cuentas a plazo fijo."));
            } else {
              /** Continúa con el registro de la cuenta */
              return generateCustomId()
                  .flatMap(a -> {
                    account.setIdAccount(a);
                    return accountRepository.save(account);
                  });
            }
          });
    }

    /**Por último, si es cliente empresarial y no entra ninguna de las lógicas anteriores;
     * pero sí múltiples cuentas corrientes continúa con el registro.*/
    return generateCustomId()
        .flatMap(a -> {
          account.setIdAccount(a);
          return accountRepository.save(account);
        });

  }

  private Mono<Boolean> hasAccountOfType(String idCustomer, String idProduct) {
    /** Utiliza el repositorio de cuentas para verificar si existe una cuenta con el mismo idCustomer e idProduct. */
    return accountRepository.existsByIdCustomerAndIdProduct(idCustomer, idProduct);
  }


  private Mono<String> generateCustomId() {
    return accountRepository.count() /** Contar la cantidad actual de documentos */
        .map(count -> {
          int nextNumber = count.intValue() + 1;
          return "A" + String.format("%04d", nextNumber);
        })
        .defaultIfEmpty("A0001"); // Si no hay documentos, comenzar desde A0001
  }

  @Override
  public Mono<Account> updateAccount(Account account, String idAccount) {

    return accountRepository.findById(idAccount)
        .flatMap(currentBankAccount -> {
          currentBankAccount.setAvailableBalance(account.getAvailableBalance());
          currentBankAccount.setHolderAccount(account.getHolderAccount());
          currentBankAccount.setAuthorizedSigner(account.getAuthorizedSigner());
          currentBankAccount.setUpdateDatetime(LocalDateTime.now());
          return accountRepository.save(currentBankAccount);
        });

  }

  @Override
  public Mono<Account> deleteAccountById(String idAccount) {
    return accountRepository.findById(idAccount)
        .flatMap(existingAccount -> accountRepository.delete(existingAccount)
            .then(Mono.just(existingAccount)));
  }

  @Override
  public Mono<Movement> addOperationToAccount(String idAccount, Movement movement) {

    return Mono.just(movement)
        .doOnNext(m -> {
          /**Genera un único Id para la operación */
          String newMovementId = UUID.randomUUID().toString();
          movement.setIdMovement(newMovementId);

          /** Convierte el movementType a mayúsculas si no es nulo */
          if (m.getMovementType() != null) {
            m.setMovementType(m.getMovementType().toUpperCase());
          }
        })
        .flatMap(m -> accountRepository.findById(idAccount)
            .switchIfEmpty(Mono.error(() -> new AccountNotFoundException(idAccount)))
            .flatMap(account -> processOperation(account, m)));

  }

  private Mono<Movement> processOperation(Account account, Movement movement) {
    String operation = movement.getOperation();
    /** Permite validar si la operación se encuentra en la lista de OperationType; */
    if (isValidOperation(operation)) {
      OperationType operationType = OperationType.valueOf(operation.toUpperCase());

      return getOperationHandler(operationType).apply(account, movement);
    } else {
      throw new IllegalArgumentException("Operación no válida: " + operation);
    }

  }

  private boolean isValidOperation(String operation) {
    try {
      OperationType.valueOf(operation);
      return true;

    } catch (IllegalArgumentException e) {
      return false;

    }
  }

  private BiFunction<Account, Movement, Mono<Movement>> getOperationHandler(OperationType operationType) {
    return (account, movement) -> {
      switch (operationType) {
        case TRANSFERIR_DINERO:
          return transferirDinero(account, movement);
        case COBRAR_GIROS:
          return realizarRetiro(account, movement);
        default:
          throw new IllegalArgumentException("Operación no válida: " + operationType);
      }
    };
  }

  private Mono<Movement> transferirDinero(Account cuentaOrigen, Movement bankMovement) {
    /** Genera un nuevo ID único para la operación de transferencia */
    String transferenciaId = UUID.randomUUID().toString();
    bankMovement.setIdMovement(transferenciaId);

    /** Recupera la cuenta de destino desde la base de datos */
    String accountNumberDestino = bankMovement.getTargetAccount();
    return accountRepository.findByAccountNumber(accountNumberDestino)
        .flatMap(destino -> {
          float monto = bankMovement.getMonto();

          /** Verifica si la cuenta de origen tiene suficiente saldo */
          if (cuentaOrigen.getAvailableBalance() >= monto) {
            // Genera un nuevo ID único para la cuenta de destino
            String destinoIdMovement = UUID.randomUUID().toString();

            /** Crea una copia de la operación de transferencia para la cuenta de destino. */
            Movement movementDestino = new Movement();
            movementDestino.setIdMovement(destinoIdMovement);
            movementDestino.setOperation(bankMovement.getOperation());
            movementDestino.setMovementType(bankMovement.getMovementType());
            movementDestino.setMoneda(bankMovement.getMoneda());
            movementDestino.setMonto(bankMovement.getMonto());
            movementDestino.setTargetAccount(bankMovement.getTargetAccount());
            movementDestino.setSourceAccount(bankMovement.getSourceAccount());
            movementDestino.setCreationDatetime(LocalDateTime.now());

            /** Realiza la transferencia de dinero. */
            cuentaOrigen.setAvailableBalance((cuentaOrigen.getAvailableBalance() - monto));
            destino.setAvailableBalance((destino.getAvailableBalance() + monto));

            /** Cambia a monto negativo en la cuenta de origen. */
            bankMovement.setMonto(-monto);

            /** Agrega la operación de transferencia a ambas cuentas con sus IDS. */
            cuentaOrigen.getBankMovements().add(bankMovement);
            destino.getBankMovements().add(movementDestino);

            /** Actualiza ambas cuentas en la base de datos */
            return accountRepository.save(cuentaOrigen)
                .then(accountRepository.save(destino))
                .then(Mono.just(bankMovement));
          } else {
            /** Si la cuenta de origen no tiene suficiente saldo, maneja el error aquí */
            return Mono.error(new RuntimeException("La cuenta de origen no tiene suficiente saldo para la transferencia."));
          }
        });
  }

  private Mono<Movement> realizarRetiro(Account account, Movement movement) {
    /** Genera un nuevo ID único para la operación de retiro */
    String retiroId = UUID.randomUUID().toString();
    movement.setIdMovement(retiroId);

    float monto = movement.getMonto();
    /** Verifica si la cuenta tiene suficiente saldo para el retiro */
    if (account.getAvailableBalance() >= monto) {
      /** Realiza el retiro restando el monto de la cuenta */
      account.setAvailableBalance(account.getAvailableBalance() - monto);
      account.getBankMovements().add(movement);

      /** Cambia a monto negativo en la cuenta de origen. */
      movement.setMonto(-monto);

      /** Actualiza la cuenta en la base de datos */
      return accountRepository.save(account)
          .then(Mono.just(movement));
    } else {
      return Mono.error(new RuntimeException("La cuenta no tiene suficiente saldo para el retiro."));
    }
  }

}
