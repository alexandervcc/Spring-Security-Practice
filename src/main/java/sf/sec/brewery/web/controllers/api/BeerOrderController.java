package sf.sec.brewery.web.controllers.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;
import sf.sec.brewery.services.BeerOrderService;
import sf.sec.brewery.web.model.BeerOrderDto;
import sf.sec.brewery.web.model.BeerOrderPagedList;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequestMapping("/api/v1/customers/{customerId}")
@RestController
public class BeerOrderController {
    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final BeerOrderService beerOrderService;

    public BeerOrderController(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @PreAuthorize("" +
            "hasAuthority('order.read') OR" +
            " hasAuthority('customer.order.read') AND" +
            " @beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId )"
    )
    @GetMapping("orders")
    public BeerOrderPagedList listOrders(
        @PathVariable("customerId") UUID customerId,
        @RequestParam(value = "pageNumber",required = false) Integer pageNumber,
        @RequestParam(value = "pageSize",required = false) Integer pageSize
    ){
        if(pageNumber==null || pageNumber<0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }
        if(pageSize==null || pageSize<0){
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return beerOrderService.listOrders(customerId, PageRequest.of(pageNumber,pageSize));
    }

    @PreAuthorize("" +
            "hasAuthority('order.create') OR" +
            " hasAuthority('customer.order.create') AND" +
            " @beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId )"
    )
    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(
            @PathVariable("customerId")UUID customerId,
            @RequestBody BeerOrderDto beerOrderDto
            ){
        return beerOrderService.placeOrder(customerId,beerOrderDto);
    }

//    @PreAuthorize("" +
//            "hasAuthority('order.read') OR" +
//            " hasAuthority('customer.order.read') AND" +
//            " @beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId )")
//    @GetMapping("orders/{orderId}")
//    public BeerOrderDto getOrder(
//            @PathVariable("customerId")UUID customerId,
//            @PathVariable("orderId")UUID orderId
//    ){
//        return beerOrderService.getOrderById(customerId,orderId);
//    }

    @GetMapping("orders/{orderId}")
    public BeerOrderDto getOrderOnlyById(
            @PathVariable("customerId")UUID customerId,
            @PathVariable("orderId")UUID orderId
    ){
        BeerOrderDto beerOrderDto = beerOrderService.getOrderById(orderId);
        if(beerOrderDto==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found");
        }

        log.debug("Found order: "+beerOrderDto);
        return beerOrderDto;
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(
            @PathVariable("customerId")UUID customerId,
            @PathVariable("orderId")UUID orderId
    ){
        beerOrderService.pickupOrder(customerId,orderId);
    }
}
