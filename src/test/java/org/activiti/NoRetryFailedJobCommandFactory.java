package org.activiti;

import org.activiti.engine.impl.cfg.TransactionContext;
import org.activiti.engine.impl.cfg.TransactionState;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.jobexecutor.FailedJobCommandFactory;
import org.activiti.engine.impl.jobexecutor.JobAddedNotification;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.activiti.engine.impl.persistence.entity.JobEntity;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author lukas
 * @since 5/7/2015
 * <p>
 * All rights reserved.
 * <p>
 * (C) Consensus Corporation
 */
public class NoRetryFailedJobCommandFactory implements FailedJobCommandFactory
{
    public Command<Object> getCommand(final String jobId, final Throwable exception)
    {
        return new Command<Object>()
        {
            public Object execute(CommandContext commandContext)
            {
                JobEntity job = Context
                        .getCommandContext()
                        .getJobEntityManager()
                        .findJobById(jobId);
                job.setRetries(0);
                job.setLockOwner(null);
                job.setLockExpirationTime(null);

                if(exception != null)
                {
                    job.setExceptionMessage(exception.getMessage());
                    job.setExceptionStacktrace(getExceptionStacktrace());
                }

                JobExecutor jobExecutor = Context.getProcessEngineConfiguration().getJobExecutor();
                JobAddedNotification jobAddedNotification = new JobAddedNotification(jobExecutor);
                TransactionContext transactionContext = commandContext.getTransactionContext();
                transactionContext.addTransactionListener(TransactionState.COMMITTED, jobAddedNotification);

                return null;
            }

            private String getExceptionStacktrace()
            {
                StringWriter stringWriter = new StringWriter();
                exception.printStackTrace(new PrintWriter(stringWriter));
                return stringWriter.toString();
            }
        };
    }

}
