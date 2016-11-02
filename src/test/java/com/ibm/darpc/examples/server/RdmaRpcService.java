/*
 * DaRPC: Data Center Remote Procedure Call
 *
 * Author: Patrick Stuedi <stu@zurich.ibm.com>
 *
 * Copyright (C) 2016, IBM Corporation
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
 *
 */

package com.ibm.darpc.examples.server;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.darpc.RpcEndpoint;
import com.ibm.darpc.RpcServerEvent;
import com.ibm.darpc.RpcService;
import com.ibm.darpc.examples.protocol.RdmaRpcProtocol;
import com.ibm.darpc.examples.protocol.RdmaRpcRequest;
import com.ibm.darpc.examples.protocol.RdmaRpcResponse;

public class RdmaRpcService extends RdmaRpcProtocol implements RpcService<RdmaRpcRequest, RdmaRpcResponse> {
	private static final Logger logger = LoggerFactory.getLogger("com.ibm.darpc");
	
	private int servicetimeout;
	
	public RdmaRpcService(int servicetimeout){
		this.servicetimeout = servicetimeout;
		logger.info("RpcService with timeout " + servicetimeout);
	}
	
	public void processServerEvent(RpcServerEvent<RdmaRpcRequest, RdmaRpcResponse> event) throws IOException {
		RdmaRpcRequest request = event.getReceiveMessage();
		RdmaRpcResponse response = event.getSendMessage();
		response.setName(request.getParam() + 1);
		if (servicetimeout > 0){
			try {
				Thread.sleep(servicetimeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("rpc service, param " + event.getReceiveMessage().getParam());
		event.triggerResponse();
	}

	@Override
	public void open(RpcEndpoint<RdmaRpcRequest, RdmaRpcResponse> endpoint) {
		logger.info("new connection " + endpoint.getEndpointId());
	}

	@Override
	public void close(RpcEndpoint<RdmaRpcRequest, RdmaRpcResponse> endpoint) {
		logger.info("disconnecting " + endpoint.getEndpointId());
	}
}
