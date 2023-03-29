/* Copyrights and Licenses
 *
 * Copyright (c) 2012-2014 by the Ministry of Justice. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * - All advertising materials mentioning features or use of this software must display the
 * following acknowledgment: "This product includes Money Claims OnLine."
 * - Products derived from this software may not be called "Money Claims OnLine" nor may
 * "Money Claims OnLine" appear in their names without prior written permission of the
 * Ministry of Justice.
 * - Redistributions of any form whatsoever must retain the following acknowledgment: "This
 * product includes Money Claims OnLine."
 * This software is provided "as is" and any expressed or implied warranties, including, but
 * not limited to, the implied warranties of merchantability and fitness for a particular purpose are
 * disclaimed. In no event shall the Ministry of Justice or its contributors be liable for any
 * direct, indirect, incidental, special, exemplary, or consequential damages (including, but
 * not limited to, procurement of substitute goods or services; loss of use, data, or profits;
 * or business interruption). However caused any on any theory of liability, whether in contract,
 * strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this
 * software, even if advised of the possibility of such damage.
 *
 * $Id: $
 * $LastChangedRevision: $
 * $LastChangedDate: $
 * $LastChangedBy: $ */

package uk.gov.moj.sdt.utils.transaction.synchronizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.transaction.synchronizer.api.IMessageSynchronizer;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of the IMessageSynchronizer interface.
 *
 * @author Manoj Kulkarni
 */
@Component("MessageSynchronizer")
public class MessageSynchronizer extends TransactionSynchronizationAdapter implements IMessageSynchronizer {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSynchronizer.class);

    @Override
    public void execute(final Runnable command) {
        // Is this thread in the context of a transaction? If not then run immediately.
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            LOGGER.debug("Transaction synchronization is NOT ACTIVE. Executing command [{}] right now", command);

            // Run one new command (usually each command is responsible for a single message).
            command.run();
            // else queue up the command to run once the transaction has completed.
        } else {
            LOGGER.debug("Submitting new command XXX to run after commit [{}]", command);

            // Add new command to thread local list.
            if (SdtContext.getContext().addSynchronisationTask(command)) {
                TransactionSynchronizationManager.registerSynchronization(this);
            }
        }
    }

    @Override
    public void afterCommit() {
        final List<Runnable> synchronisationTasks = SdtContext.getContext().getSynchronisationTasks();

        LOGGER.debug("Transaction successfully committed, so executing the tasks in the list asynchronously");

        final ExecuteRunnable thread = new ExecuteRunnable(Collections.unmodifiableList(synchronisationTasks));
        thread.start();
        LOGGER.debug("Started asynchronouse thread to write messages.");
    }

    @Override
    public void afterCompletion(final int status) {
        LOGGER.debug("Transaction completed with status {}", (status == STATUS_COMMITTED ? "Committed" : "Rollback"));
    }

    @Override
    public void synchronizeTask(final Runnable task) {
        // Delegate processing to the execute method.
        this.execute(task);
    }

    /**
     * Custom thread to execute a collection of <code>Runnable</code> instances.
     *
     * @author d276205
     */
    private class ExecuteRunnable extends Thread {
        /**
         * Runnable instances.
         */
        private List<Runnable> runnables;

        /**
         * Constructs an instance of ExecuteRunnable.
         *
         * @param runnables collection of Runnable instances.
         */
        ExecuteRunnable(final List<Runnable> runnables) {
            if ((runnables == null) || (runnables.isEmpty())) {
                throw new IllegalArgumentException("The 'runnables' collection should neither be null nor empty");
            }
            this.runnables = runnables;
        }

        @Override
        public void run() {
            LOGGER.debug("Processing {} instances of runnable", runnables.size());

            for (Runnable runnable : runnables) {
                LOGGER.debug("Executing task {}", runnable);
                runnable.run();
            }
        }
    }

}
