/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.console.ng.ht.client.editors.taskdetailsmulti;

import com.github.gwtbootstrap.client.ui.Heading;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.console.ng.ht.client.i18n.Constants;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated(value = "TaskDetailsMultiViewImpl.html")
public class TaskDetailsMultiViewImpl extends Composite implements TaskDetailsMultiPresenter.TaskDetailsMultiView {

    private TaskDetailsMultiPresenter presenter;

    
    @DataField
    public Heading taskIdAndName = new Heading(4);

   

    
    @DataField
    public HTMLPanel content = new HTMLPanel("");
    
  
    @Inject
    private Event<NotificationEvent> notification;

    private Constants constants = GWT.create( Constants.class );

    @Override
    public void init( TaskDetailsMultiPresenter presenter ) {
        this.presenter = presenter;

    }


    @Override
    public void displayNotification( String text ) {
        notification.fire( new NotificationEvent( text ) );
    }

    

    public HTMLPanel getContent() {
        return content;
    }

    public Heading getTaskIdAndName() {
        return taskIdAndName;
    }

    
    

}
