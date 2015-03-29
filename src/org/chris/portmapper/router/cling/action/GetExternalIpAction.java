/**
 *
 */
package org.chris.portmapper.router.cling.action;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;

public class GetExternalIpAction extends AbstractClingAction<String> {

    public GetExternalIpAction(final RemoteService service) {
        super(service, "GetExternalIPAddress");
    }

    @Override
    public String convert(final ActionInvocation<?> invocation) {
        return (String) invocation.getOutput("NewExternalIPAddress").getValue();
    }

}
