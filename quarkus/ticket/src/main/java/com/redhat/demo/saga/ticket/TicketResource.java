package com.redhat.demo.saga.ticket;

import com.redhat.demo.saga.ticket.model.Ticket;
import com.redhat.demo.saga.ticket.rest.ErrorMessage;
import com.redhat.demo.saga.ticket.rest.TicketDto;
import com.redhat.demo.saga.ticket.service.TicketService;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketResource.class);

    @Inject
    TicketService ticketService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @POST
    @Counted(name = "performedChecks", monotonic = true, description = "Number of add tickets")
    @Timed(name = "checksTimer", description = "Add ticket duration", unit = MetricUnits.MILLISECONDS)
    public Response add(TicketDto ticketDto) {
        Ticket ticket = createTicket(ticketDto);
        ticket = ticketService.bookTicket(ticket);

        ticketDto.setId(ticket.getId());
        ticketDto.setMessageOnTicket(ticket.getMessageOnTicket());
        ticketDto.setMessageSeverityTicket(ticket.getMessageSeverityTicket());


        if(ticketDto.getMessageSeverityTicket()!= null && ticketDto.getMessageSeverityTicket().equals("ERROR")) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setMessage(ticketDto.getMessageOnTicket());
            errorMessage.setSeverity(ticketDto.getMessageSeverityTicket());
            return Response.status(500).entity(errorMessage).build();
        }

        return Response.ok(ticketDto).build();
    }

    private Ticket createTicket(TicketDto ticketDto) {
        Ticket ticket = new Ticket();
        ticket.setAccountId(ticketDto.getAccountId());
        ticket.setName(ticketDto.getName());
        ticket.setNumberOfPersons(ticketDto.getNumberOfPersons());
        ticket.setOrderId(ticketDto.getOrderId());
        ticket.setTotalCost(ticketDto.getTotalCost());
        ticket.setInsuranceRequired(ticketDto.getInsuranceRequired());
        return ticket;
    }

}