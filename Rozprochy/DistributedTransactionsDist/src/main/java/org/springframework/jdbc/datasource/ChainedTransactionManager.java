package org.springframework.jdbc.datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.TransactionException;
import org.springframework.transaction.MultiTransactionStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ChainedTransactionManager implements PlatformTransactionManager {

	protected Log logger = LogFactory.getLog(getClass());

	private List<PlatformTransactionManager> transactionManagers = new ArrayList<PlatformTransactionManager>();

	public void setTransactionManagers(
			List<PlatformTransactionManager> transactionManagers) {
		this.transactionManagers = transactionManagers;
	}

	public void setTransactionManagers(
			PlatformTransactionManager... transactionManagers) {
		this.transactionManagers = new ArrayList(
				Arrays.asList(transactionManagers));
	}

	@Override
	public TransactionStatus getTransaction(TransactionDefinition definition)
			throws TransactionException {

		MultiTransactionStatus mts = new MultiTransactionStatus(
				transactionManagers.get(0)/* First TM is main TM */);

		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.initSynchronization();
			mts.setNewSynchonization();
		}

		for (PlatformTransactionManager transactionManager : transactionManagers) {
			mts.getTransactionStatuses().put(transactionManager,
					transactionManager.getTransaction(definition));
		}

		return mts;
	}

	@Override
	public void commit(TransactionStatus status) throws TransactionException {

		boolean commit = true;
		Exception commitException = null;
		PlatformTransactionManager commitExceptionTransactionManager = null;

		for (int i = transactionManagers.size() - 1; i >= 0; i--) {
			PlatformTransactionManager transactionManager = transactionManagers
					.get(i);

			if (commit) {
				try {
					transactionManager.commit(((MultiTransactionStatus) status)
							.getTransactionStatuses().get(transactionManager));
				} catch (Exception ex) {
					commit = false;
					commitException = ex;
					commitExceptionTransactionManager = transactionManager;
				}
			} else {
				// after unsucessfull commitu we must try to rollback rest of
				// datasouces
				try {
					transactionManager
							.rollback(((MultiTransactionStatus) status)
									.getTransactionStatuses().get(
											transactionManager));
				} catch (Exception ex) {
					logger.warn("Rollback exception (after commit) ("
							+ transactionManager + ") " + ex.getMessage(), ex);
				}
			}
		}

		if (((MultiTransactionStatus) status).isNewSynchonization()) {
			TransactionSynchronizationManager.clear();
		}

		if (commitException != null) {
			throw new TransactionSystemException("Commit exception ("
					+ commitExceptionTransactionManager + ") "
					+ commitException.getMessage(), commitException);
		}

	}

	@Override
	public void rollback(TransactionStatus status) throws TransactionException {

		Exception rollbackException = null;
		PlatformTransactionManager rollbackExceptionTransactionManager = null;

		for (int i = transactionManagers.size() - 1; i >= 0; i--) {
			PlatformTransactionManager transactionManager = transactionManagers
					.get(i);

			// bez ohledu na exception se musim pokusit rollbacknout vsechny
			// datasource
			try {
				transactionManager.rollback(((MultiTransactionStatus) status)
						.getTransactionStatuses().get(transactionManager));
			} catch (Exception ex) {
				if (rollbackException == null) {
					rollbackException = ex;
					rollbackExceptionTransactionManager = transactionManager;
				} else {
					logger.warn("Rollback exception (" + transactionManager
							+ ") " + ex.getMessage(), ex);
				}
			}
		}

		if (((MultiTransactionStatus) status).isNewSynchonization()) {
			TransactionSynchronizationManager.clear();
		}

		if (rollbackException != null) {
			throw new TransactionSystemException("Rollback exception ("
					+ rollbackExceptionTransactionManager + ") "
					+ rollbackException.getMessage(), rollbackException);
		}
	}

}
