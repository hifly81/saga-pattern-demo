package com.redhat.demo.saga.insurance;

import com.redhat.demo.saga.insurance.model.Insurance;
import com.redhat.demo.saga.insurance.rest.ErrorMessage;
import com.redhat.demo.saga.insurance.rest.InsuranceDto;
import com.redhat.demo.saga.insurance.service.InsuranceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/insurances")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InsuranceResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsuranceResource.class);

    @Inject
    InsuranceService insuranceService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @POST
    public Response add(InsuranceDto insuranceDto) {

        Insurance insurance = createInsurance(insuranceDto);
        insurance = insuranceService.bookInsurance(insurance);

        insuranceDto.setId(insurance.getId());
        insuranceDto.setMessageOnTicket(insurance.getMessageOnTicket());
        insuranceDto.setMessageSeverityTicket(insurance.getMessageSeverityTicket());


        if(insuranceDto.getMessageSeverityTicket()!= null && insuranceDto.getMessageSeverityTicket().equals("ERROR")) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setMessage(insuranceDto.getMessageOnTicket());
            errorMessage.setSeverity(insuranceDto.getMessageSeverityTicket());
            return Response.status(500).entity(errorMessage).build();
        }

        return Response.ok(insuranceDto).build();
    }

    private Insurance createInsurance(InsuranceDto insuranceDto) {
        Insurance insurance = new Insurance();
        insurance.setInsuranceCost(insuranceDto.getInsuranceCost());
        insurance.setName(insuranceDto.getName());
        insurance.setAccountId(insuranceDto.getAccountId());
        insurance.setTicketId(insuranceDto.getTicketId());
        insurance.setOrderId(insuranceDto.getOrderId());

        return insurance;
    }

}