/*
 * Copyright 2016 ThoughtWorks, Inc.
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

package cd.go.contrib.elasticagents.openstack;

import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.DefaultGoApiRequest;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;

import java.util.Collection;

import static cd.go.contrib.elasticagents.openstack.Constants.*;

public class PluginRequest {
    private final GoApplicationAccessor accessor;

    public static final Logger LOG = Logger.getLoggerFor(OpenStackPlugin.class);

    public PluginRequest(GoApplicationAccessor accessor) {
        this.accessor = accessor;
    }

    public PluginSettings getPluginSettings() throws ServerRequestFailedException {
        DefaultGoApiRequest request = new DefaultGoApiRequest(REQUEST_SERVER_GET_PLUGIN_SETTINGS, PLUGIN_SETTINGS_PROCESSOR_API_VERSION, PLUGIN_IDENTIFIER);
        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            throw ServerRequestFailedException.getPluginSettings(response);
        }

        return PluginSettings.fromJSON(response.responseBody());
    }

    public Agents listAgents() throws ServerRequestFailedException {
        DefaultGoApiRequest request = new DefaultGoApiRequest(REQUEST_SERVER_LIST_AGENTS, ELASTIC_AGENT_PROCESSOR_API_VERSION, PLUGIN_IDENTIFIER);
        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            throw ServerRequestFailedException.listAgents(response);
        }

        return new Agents(Agent.fromJSONArray(response.responseBody()));
    }

    public void disableAgents(Collection<Agent> toBeDisabled) throws ServerRequestFailedException {

        if (toBeDisabled.isEmpty()) {
            return;
        }

        DefaultGoApiRequest request = new DefaultGoApiRequest(PROCESS_DISABLE_AGENTS, ELASTIC_AGENT_PROCESSOR_API_VERSION, PLUGIN_IDENTIFIER);

        request.setRequestBody(Agent.toJSONArray(toBeDisabled));

        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            throw ServerRequestFailedException.disableAgents(response);
        }
    }

    public void deleteAgents(Collection<Agent> toBeDeleted) throws ServerRequestFailedException {
        if (toBeDeleted.isEmpty()) {
            return;
        }

        DefaultGoApiRequest request = new DefaultGoApiRequest(PROCESS_DELETE_AGENTS, ELASTIC_AGENT_PROCESSOR_API_VERSION, PLUGIN_IDENTIFIER);
        request.setRequestBody(Agent.toJSONArray(toBeDeleted));
        GoApiResponse response = accessor.submit(request);

        if (response.responseCode() != 200) {
            throw ServerRequestFailedException.deleteAgents(response);
        }
    }
}
