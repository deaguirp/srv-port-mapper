package com.aws.portmapper.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.SessionScope;

import com.offbynull.portmapper.PortMapperFactory;
import com.offbynull.portmapper.gateway.Bus;
import com.offbynull.portmapper.gateway.Gateway;
import com.offbynull.portmapper.gateways.network.NetworkGateway;
import com.offbynull.portmapper.gateways.process.ProcessGateway;
import com.offbynull.portmapper.mapper.PortMapper;

/*
 * @RestController
@SessionScope
@RequestMapping("/mi")
 * @RequestMapping(value = "/file/{usuario}/{path}/{filename:.+}", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> file(@PathVariable String path, @PathVariable String usuario, @PathVariable String filename) throws IOException
 */

@SessionScope
@RequestMapping("/mappers")
@RestController
public class MapperManager {

	private boolean shutdown;
	private List<PortMapper> mappers;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
/*	public Modulo list(@RequestBody Modulo modulo,  HttpServletRequest request) {*/
	public List<Object> list() throws InterruptedException {
		checkConnection();
		if (shutdown)
			return Collections.emptyList();
		return this.mappers.stream().map(Object::toString).collect(Collectors.toList());
	}

	@RequestMapping(value = "/liststr", method = RequestMethod.GET)
	public String[] listStr() throws InterruptedException {
		checkConnection();
		if (shutdown)
			return new String[]{};
		return new String[]{this.mappers.toString()};
	}

	@RequestMapping(value = "/{mapperNro}/view", method = RequestMethod.GET)
	public Object viewMaper(@PathVariable int mapperNro) throws InterruptedException {
		checkConnection();
		if (shutdown)
			return new String[]{};
		return new Object(){
			public int getNum(){ return mapperNro; }
			public String getMapper(){ return mappers.get(mapperNro).toString();}
		};
	}

	private synchronized void checkConnection() throws InterruptedException {
		if (mappers != null)
			return;
		// Start gateways
		Gateway network = NetworkGateway.create();
		Gateway process = ProcessGateway.create();
		Bus networkBus = network.getBus();
		Bus processBus = process.getBus();

		// Discover port forwarding devices and take the first one found
		mappers = PortMapperFactory.discover(networkBus, processBus);
	}

	
}
