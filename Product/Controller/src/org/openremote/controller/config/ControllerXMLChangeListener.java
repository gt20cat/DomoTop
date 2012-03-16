/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.controller.config;

import org.openremote.controller.service.ControllerXMLChangeService;
import org.openremote.controller.spring.SpringContext;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 
 * @author handy.wang 2010-03-19
 *
 */
public class ControllerXMLChangeListener extends QuartzJobBean {

   private ControllerXMLChangeService controllerXMLChangeService = (ControllerXMLChangeService)SpringContext.getInstance().getBean("controllerXMLChangeService");
   
   // The entry of Quartz job
   @Override
   protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
      controllerXMLChangeService.refreshController();
   }
   
}