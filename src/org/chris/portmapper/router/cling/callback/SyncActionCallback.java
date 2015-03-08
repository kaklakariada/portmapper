/**
 *
 */
package org.chris.portmapper.router.cling.callback;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.chris.portmapper.router.cling.ClingRouterException;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SyncActionCallback<T> extends ClingActionCallback {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AtomicReference<T> result = new AtomicReference<>();

    protected SyncActionCallback(final RemoteService service, final ControlPoint controlPoint, final String action) {
        super(service, controlPoint, action);
    }

    protected SyncActionCallback(final RemoteService service, final ControlPoint controlPoint, final String action,
            final List<ActionArgumentValue<RemoteService>> arguments) {
        super(service, controlPoint, action, arguments);
    }

    @Override
    public void success(@SuppressWarnings("rawtypes") final ActionInvocation invocation) {
        final T newResult = convertResult(invocation);
        logger.debug("Converted {} to {}", invocation, newResult);
        final boolean success = result.compareAndSet(null, newResult);
        if (!success) {
            throw new ClingRouterException("Got duplicate results: " + newResult + ", " + result.get());
        }
    }

    protected abstract T convertResult(ActionInvocation<?> invocation);

    public T execute() {
        this.run();
        return result.get();
    }
}
