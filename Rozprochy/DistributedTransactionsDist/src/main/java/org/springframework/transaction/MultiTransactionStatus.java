package org.springframework.transaction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MultiTransactionStatus implements TransactionStatus {

	private PlatformTransactionManager mainTransactionManager;

	private Map<PlatformTransactionManager, TransactionStatus> transactionStatuses = Collections
			.synchronizedMap(new HashMap<PlatformTransactionManager, TransactionStatus>());

	private boolean newSynchonization;

	public Map<PlatformTransactionManager, TransactionStatus> getTransactionStatuses() {
		return transactionStatuses;
	}

	public MultiTransactionStatus(
			PlatformTransactionManager mainTransactionManager) {
		this.mainTransactionManager = mainTransactionManager;
	}

	private TransactionStatus getHlavniTM() {
		return transactionStatuses.get(mainTransactionManager);
	}

	public void setNewSynchonization() {
		this.newSynchonization = true;
	}

	public boolean isNewSynchonization() {
		return newSynchonization;
	}

	@Override
	public boolean isNewTransaction() {
		return getHlavniTM().isNewTransaction();
	}

	@Override
	public boolean hasSavepoint() {
		return getHlavniTM().hasSavepoint();
	}

	@Override
	public void setRollbackOnly() {
		for (TransactionStatus ts : transactionStatuses.values()) {
			ts.setRollbackOnly();
		}
	}

	@Override
	public boolean isRollbackOnly() {
		return getHlavniTM().isRollbackOnly();
	}

	@Override
	public boolean isCompleted() {
		return getHlavniTM().isCompleted();
	}

	@Override
	public Object createSavepoint() throws TransactionException {
		//throw new FrameworkRTException(");
		throw new NestedTransactionNotSupportedException("Savepoint not supported");
		// return getHlavniTM().createSavepoint();
	}

	@Override
	public void rollbackToSavepoint(Object savepoint)
			throws TransactionException {
		for (TransactionStatus ts : transactionStatuses.values()) {
			ts.rollbackToSavepoint(savepoint);
		}
	}

	@Override
	public void releaseSavepoint(Object savepoint) throws TransactionException {
		for (TransactionStatus ts : transactionStatuses.values()) {
			ts.releaseSavepoint(savepoint);
		}
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

}
