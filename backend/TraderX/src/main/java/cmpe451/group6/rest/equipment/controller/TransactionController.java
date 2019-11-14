package cmpe451.group6.rest.equipment.controller;

import cmpe451.group6.Util;
import cmpe451.group6.rest.equipment.dto.EquipmentMetaWrapper;
import cmpe451.group6.rest.equipment.dto.TransactionDTO;
import cmpe451.group6.rest.equipment.model.Equipment;
import cmpe451.group6.rest.equipment.model.Transaction;
import cmpe451.group6.rest.equipment.service.TransactionService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static cmpe451.group6.authorization.exception.GlobalExceptionHandlerController.GENERIC_ERROR_RESPONSE;


@RestController
@RequestMapping("/transaction")
@Api(tags = "Transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private Util util;

    @GetMapping(value = "/{username}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_BASIC') or hasRole('ROLE_TRADER')")
    @ApiOperation(value = "Gets transactions of an user", response = Transaction.class)
    @ApiResponses(value = {
            @ApiResponse(code = 417, message = "No such an equipment found.")})
    public List<Transaction> getTransactionsByUser(@ApiParam("User Name") @PathVariable String username, HttpServletRequest req) {
        return transactionService.getTransactionsByUser(username, util.unwrapUsername(req));
    }

    @GetMapping(value = "/{code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns transactions on specified equipment code (no auth required)", response = TransactionDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = GENERIC_ERROR_RESPONSE)})
    public List<TransactionDTO> getTransactionsByCode(@ApiParam("User Name") @PathVariable String code) {
        return transactionService.getTransactionsByCode(code);
    }

    @GetMapping(value = "/all")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns all transactions (no auth required)", response = TransactionDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = GENERIC_ERROR_RESPONSE)})
    public List<TransactionDTO> getAllTransactions() {
        return transactionService.getTransactions();
    }

    @GetMapping(value = "/byDate")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns transactions in between start date and end date (no auth required)", response = TransactionDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = GENERIC_ERROR_RESPONSE)})
    public List<TransactionDTO> getTransactionsByDateBetween(@ApiParam("Start Date") @RequestParam Date start, @ApiParam("End Date") @RequestParam Date end ) {
        return transactionService.getTransactionByDateBetween(start, end);
    }

    @GetMapping(value = "/count/all")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns number of all transactions  (no auth required)", response = Integer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = GENERIC_ERROR_RESPONSE)})
    public int countAllTransactions() {
        return transactionService.numberOfTransactions();
    }

    @GetMapping(value = "/count/{username}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_BASIC') or hasRole('ROLE_TRADER')")
    @ApiOperation(value = "Returns number of all transactions made by specific user ", response = Integer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = GENERIC_ERROR_RESPONSE)})
    public int countAllTransactions(@ApiParam("User Name") @PathVariable String username, HttpServletRequest req) {
        return transactionService.numberOfTransactionByUser(username, util.unwrapUsername(req));
    }

    @GetMapping(value = "/count/{code}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns number of all transactions made by specific user (no auth required) ", response = Integer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = GENERIC_ERROR_RESPONSE)})
    public int countAllTransactions(@ApiParam("User Name") @PathVariable String code) {
        return transactionService.numberOfTransactionByCode(code);
    }

}




