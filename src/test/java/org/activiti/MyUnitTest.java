package org.activiti;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiActivityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MyUnitTest {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();
	
	@Test
	@Deployment(resources = {"org/activiti/test/my-process.bpmn20.xml"})
	public void test() throws InterruptedException
	{
		ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceByKey("my-process");
		assertNotNull(processInstance);

		activitiRule.getRuntimeService().addEventListener(new ActivitiEventListener()
		{
			public void onEvent(ActivitiEvent activitiEvent)
			{
				ActivitiEventType eventType = activitiEvent.getType();

				if(!eventType.equals(ActivitiEventType.ENTITY_CREATED) && !eventType.equals(ActivitiEventType.ENTITY_INITIALIZED) && !eventType.equals(ActivitiEventType.ENTITY_DELETED))
				{
					System.out.println("Workflow			=> " + activitiEvent.getProcessDefinitionId() + " ID: " + activitiEvent.getProcessInstanceId());
					System.out.println("Event:				=> " + activitiEvent.getType());

					if (eventType.equals(ActivitiEventType.ACTIVITY_STARTED) || eventType.equals(ActivitiEventType.ACTIVITY_COMPLETED))
					{
						ActivitiActivityEventImpl impl = (ActivitiActivityEventImpl) activitiEvent;
						System.out.println("Activity:			=> " + impl.getActivityId());
					}
					else if(eventType.equals(ActivitiEventType.ACTIVITY_ERROR_RECEIVED))
					{
						System.out.println("Error:			=> ");
					}

					System.out.println("-----------------------------------------");
				}
			}

			public boolean isFailOnException() {
				return false;
			}
		});

		int exit = 10;

		while(exit != 0)
		{
			exit--;
			Thread.sleep(1000);
		}
	}
}
