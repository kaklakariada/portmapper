/**
 *
 */
package org.chris.portmapper.router.cling.callback;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;

public class GetExternalIpSync extends SyncActionCallback<String> {

    public GetExternalIpSync(final RemoteService service, final ControlPoint controlPoint) {
        super(service, controlPoint, "GetExternalIPAddress");
    }

    @Override
    protected String convertResult(final ActionInvocation<?> invocation) {
        return (String) invocation.getOutput("NewExternalIPAddress").getValue();
    }
}
