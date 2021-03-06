/*
 * Jigasi, the JItsi GAteway to SIP.
 *
 * Copyright @ 2015 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.jigasi;

import java.util.*;

public class GatewaySessions
    implements GatewayListener
{
    private final Object syncRoot = new Object();

    private List<SipGatewaySession> sessions = null;

    private final SipGateway gateway;

    public GatewaySessions(SipGateway gateway)
    {
        this.gateway = gateway;

        this.gateway.addGatewayListener(this);
    }

    @Override
    public void onSessionAdded(AbstractGatewaySession session)
    {
        sessions = gateway.getActiveSessions();

        synchronized (syncRoot)
        {
            syncRoot.notifyAll();
        }
    }

    @Override
    public void onSessionRemoved(AbstractGatewaySession session)
    {}

    @Override
    public void onSessionFailed(
        AbstractGatewaySession session)
    {}

    /**
     * Obtain sessions
     * @param timeout the time to wait to receive the sessions
     * @return list of sip gw sessions
     */
    public List<SipGatewaySession> getSessions(long timeout)
        throws InterruptedException
    {
        if (sessions == null)
        {
            synchronized (syncRoot)
            {
                syncRoot.wait(timeout);
            }
        }

        this.gateway.removeGatewayListener(this);

        return sessions;
    }
}
