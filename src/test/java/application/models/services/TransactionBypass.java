package application.models.services;

import application.data.config.TransactionManager;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

final class TransactionBypass {

    private TransactionBypass() {
    }

    static MockedStatic<TransactionManager> install() {
        MockedStatic<TransactionManager> mocked = Mockito.mockStatic(TransactionManager.class);
        mocked.when(() -> TransactionManager.runInTransaction(Mockito.any()))
                .thenAnswer(invocation -> {
                    TransactionManager.TransactionalOperation<?> operation = invocation.getArgument(0);
                    return operation.run();
                });
        return mocked;
    }
}
