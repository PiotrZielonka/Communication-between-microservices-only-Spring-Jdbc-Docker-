package com.task.credit.service.impl;

import com.task.credit.domain.Credit;
import com.task.credit.repository.CreditRepository;
import com.task.credit.service.CreditService;
import com.task.credit.service.dto.CreditCustomerProductDto;
import com.task.credit.service.dto.CreditDto;
import com.task.credit.service.mapper.CreditMapper;
import com.task.customer.service.dto.CustomerDto;
import com.task.product.service.dto.ProductDto;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service
public class CreditServiceImpl implements CreditService {

  private final Logger log = LoggerFactory.getLogger(CreditServiceImpl.class);

  private final CreditRepository creditRepository;

  private final CreditMapper creditMapper;

  RestTemplate restTemplate = new RestTemplate();


  public CreditServiceImpl(CreditRepository creditRepository,
      CreditMapper creditMapper) {
    this.creditRepository = creditRepository;
    this.creditMapper = creditMapper;
  }

  @Override
  @Transactional
  public CreditDto save(CreditCustomerProductDto creditCustomerProductDto) {
    CreditDto creditDto = new CreditDto();
    creditDto.setCreditName(creditCustomerProductDto.getCreditName());
    log.debug("Request to save Credit : {}", creditDto);
    Credit credit = creditMapper.toEntity(creditDto);
    credit = creditRepository.save(credit);

    return creditMapper.toDto(credit);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CreditCustomerProductDto> findAll() {
    log.debug("Request to get all Credits Customers and Products");

    return setCustomerDtoFromCustomerMicroserviceToEveryCreditCustomerProductDto();
  }

  private List<CreditCustomerProductDto>
      setCustomerDtoFromCustomerMicroserviceToEveryCreditCustomerProductDto() {
    List<Credit> creditList = creditRepository.findAll();
    List<CreditCustomerProductDto> listOfCreditCustomerProductDto = new ArrayList<>();

    for (Credit credit : creditList) {
      CreditCustomerProductDto creditCustomerProductDto
          = creditMapper.toDtoCreditCustomerProductDto(credit);

      creditCustomerProductDto.setCustomerDto(
          getCustomerDtoByCreditIdFromCustomerMicroservice(credit.getId()));

      creditCustomerProductDto.setProductDto(
          getProductDtoByCreditIdFromProductMicroservice(credit.getId()));

      listOfCreditCustomerProductDto.add(creditCustomerProductDto);
    }

    return listOfCreditCustomerProductDto;
  }

  private CustomerDto getCustomerDtoByCreditIdFromCustomerMicroservice(Long creditId) {
    return restTemplate.getForObject("http://localhost:8081/customers/" + creditId, CustomerDto.class);
  }

  private ProductDto getProductDtoByCreditIdFromProductMicroservice(Long creditId) {
    return restTemplate.getForObject("http://localhost:8082/products/" + creditId, ProductDto.class);
  }
}
