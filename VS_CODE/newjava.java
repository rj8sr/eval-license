package com.way.parking.consumer.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.way.exception.constants.ExceptionModule;
import com.way.exception.util.BaseService;
import com.way.exception.util.WayDaoException;
import com.way.exception.util.WayServiceException;
import com.way.parking.consumer.constants.ParkingConsumerConstants;
import com.way.parking.consumer.dto.ActivatedCreditLogDto;
import com.way.parking.consumer.dto.AdditionalFeeDto;
import com.way.parking.consumer.dto.CreditVarientReservationDto;
import com.way.parking.consumer.dto.GlobalParkingPass;
import com.way.parking.consumer.dto.GlobalPrice;
import com.way.parking.consumer.dto.LastSuccessfullSmartAccessEventDto;
import com.way.parking.consumer.dto.NonCreditVarientReservationDto;
import com.way.parking.consumer.dto.ParkingLots;
import com.way.parking.consumer.dto.ParkingPassCity;
import com.way.parking.consumer.dto.ParkingPassCityDto;
import com.way.parking.consumer.dto.ParkingPassCityResponseDto;
import com.way.parking.consumer.dto.ParkingPassDetailsDto;
import com.way.parking.consumer.dto.ParkingPassInfoResponseDto;
import com.way.parking.consumer.dto.ParkingType;
import com.way.parking.consumer.dto.ReservationDto;
import com.way.parking.consumer.dto.ReservationsDayDto;
import com.way.parking.consumer.dto.SmartAccessDto;
import com.way.parking.consumer.dto.VehicleDetailsDto;
import com.way.parking.consumer.repository.ParkingPassRepository;
import com.way.util.dateutil.DateUtils;
import com.way.util.dtoutil.AddressDto;
import com.way.util.dtoutil.ImageDto;
import com.way.util.dtoutil.OperatingDay;
import com.way.util.dtoutil.OperatingHours;
import com.way.util.enumutil.Day;
import com.way.util.enumutil.PricingType;
import com.way.util.imageutil.DocumentType;

@Service
public class ParkingPassServiceImpl extends BaseService implements ParkingPassService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ParkingPassRepository parkingPassRepository;

	@Override
	public ParkingPassDetailsDto getParkingPassDetails(Integer userId) throws WayServiceException {

		String methodArgs = getExceptionUtil().methodInputArgsAsString(userId);


		ParkingPassDetailsDto detailsDto = new ParkingPassDetailsDto();

		try {

			List<Object[]> parkingPassDetails = parkingPassRepository.fetchParkingPassDetails();

			if(parkingPassDetails != null && parkingPassDetails.size() > 0) {

				detailsDto = new ParkingPassDetailsDto((Integer) parkingPassDetails.get(0)[0], (String) parkingPassDetails.get(0)[1],
						(String) parkingPassDetails.get(0)[2], (String) parkingPassDetails.get(0)[3],(String) parkingPassDetails.get(0)[4]);

				List<Object[]> reviewDetails = parkingPassRepository.fetchParkingPassRatings(detailsDto.getListingId());

				if(reviewDetails != null && reviewDetails.size() > 0) {
					detailsDto.setRatingCount((BigInteger)reviewDetails.get(0)[0]);
					detailsDto.setAvgRating((BigDecimal) reviewDetails.get(0)[1]);
				}
				//fetch global parking pass
				List<Object[]> globalPasses = parkingPassRepository.fetchGlobalParkingPassListing(detailsDto.getListingId());

				GlobalParkingPass globalParkingPass = new GlobalParkingPass();

				if(globalPasses != null && globalPasses.size() > 0) {

					List<AdditionalFeeDto> fees = new ArrayList<>();
					List<GlobalPrice> prices = new ArrayList<>();

					for(Object[] globalPass : globalPasses) {

						if(globalParkingPass.getParkingPassId() == null) {

							globalParkingPass.setParkingPassId((Integer) globalPass[0]);
							globalParkingPass.setParkingPassName((String) globalPass[1]);
							globalParkingPass.setParkingLotCount((BigInteger) globalPass[4]);
							globalParkingPass.setTimezone("America/Los_Angeles");

							BigDecimal dailyPrice = (BigDecimal) globalPass[2];
							BigDecimal monthlyPrice = (BigDecimal) globalPass[3];

							if (dailyPrice != null) {
								GlobalPrice globalPrice = new GlobalPrice();
								globalPrice.setPricingType(PricingType.Daily);
								globalPrice.setPrice(dailyPrice);
								prices.add(globalPrice);
							}

							if (monthlyPrice != null) {
								GlobalPrice globalPrice = new GlobalPrice();
								globalPrice.setPricingType(PricingType.Monthly);
								globalPrice.setPrice(monthlyPrice);
								prices.add(globalPrice);
							}

							globalParkingPass.setPrices(prices);
						}
						if(globalPass[5] != null) {
							fees.add(new AdditionalFeeDto((Integer) globalPass[5], (String) globalPass[6], (String) globalPass[7]));
							globalParkingPass.setAdditionalFees(fees);
						}

					}
				}

				detailsDto.setGlobalParkingPass(globalParkingPass);

				List<Object[]> passList = parkingPassRepository.fetchParkingPassListings(detailsDto.getListingId());

				if(passList != null && passList.size() > 0) {

					Map<PricingType, ParkingType> parkingTypeMap = new HashMap<>();

					for(Object[] parkingType : passList) {

						BigDecimal dailyPrice = (BigDecimal) parkingType[3];
						BigDecimal monthlyPrice = (BigDecimal) parkingType[4];
						Integer parkingPassId = (Integer) parkingType[0];

						if(dailyPrice != null) {
                            constructParkingPass(parkingTypeMap, parkingType, PricingType.Daily, dailyPrice, parkingPassId);
						}

						if(monthlyPrice != null) {
                            constructParkingPass(parkingTypeMap, parkingType, PricingType.Monthly, monthlyPrice, parkingPassId);
						}
					}

					detailsDto.setParkingTypes(parkingTypeMap.values().stream().collect(Collectors.toList()));
				}
			}




		} catch (Exception ex) {
			handleExceptions(ex, logger, userId, ExceptionModule.PARKING_CONSUMER, this.getClass(), "getParkingPassDetails",
					methodArgs);
		}

		return detailsDto;

	}

	private ParkingPassCity constructParkingPassCity(Object[] parkingType, BigDecimal price, Integer parkingPassId) {

		ParkingPassCity passCity = new ParkingPassCity();
		passCity.setCity((String)parkingType[2]);
		passCity.setParkingPassId(parkingPassId);
		passCity.setParkingPassName((String)parkingType[1]);
		passCity.setLat((String)parkingType[5]);
		passCity.setLon((String)parkingType[6]);
		passCity.setParkingLotCount((BigDecimal)parkingType[7]);
		passCity.setPrice(price);
		passCity.setTimezone((String)parkingType[11]);

		return passCity;
	}

	private void constructParkingPass(Map<PricingType, ParkingType> parkingTypeMap, Object[] parkingType, PricingType pricingType,
									  BigDecimal price, Integer parkingPassId) {

		if(parkingTypeMap.containsKey(pricingType)) {

			ParkingType parkingType1 = parkingTypeMap.get(pricingType);

			Map<Integer, ParkingPassCity> cityMap = parkingType1.getCityMap();

			if(cityMap.containsKey(parkingPassId)) {

				ParkingPassCity passCity = cityMap.get(parkingPassId);
				if(parkingType[8] != null) {
					passCity.getAdditionalFees().add(new AdditionalFeeDto((Integer) parkingType[8], (String) parkingType[9], (String) parkingType[10]));
				}
				cityMap.put(parkingPassId, passCity);

			} else {

				ParkingPassCity passCity = constructParkingPassCity(parkingType, price, parkingPassId);
				List<AdditionalFeeDto> feeDtos = new ArrayList<>();
				if(parkingType[8] != null) {
					feeDtos.add(new AdditionalFeeDto((Integer) parkingType[8], (String) parkingType[9], (String) parkingType[10]));
				}
				passCity.setAdditionalFees(feeDtos);
				cityMap.put(parkingPassId, passCity);

			}
			parkingType1.setCityMap(cityMap);

		} else {

			ParkingType parkingType1 = new ParkingType();
			parkingType1.setPricingType(pricingType);

			Map<Integer, ParkingPassCity> cityMap = new HashMap<>();

			ParkingPassCity passCity = constructParkingPassCity(parkingType, price, parkingPassId);
			List<AdditionalFeeDto> feeDtos = new ArrayList<>();
			if(parkingType[8] != null) {
				feeDtos.add(new AdditionalFeeDto((Integer) parkingType[8], (String) parkingType[9], (String) parkingType[10]));
			}
			passCity.setAdditionalFees(feeDtos);

			cityMap.put(parkingPassId, passCity);
			parkingType1.setCityMap(cityMap);

			parkingTypeMap.put(pricingType, parkingType1);
		}
	}

	@Override
	public ParkingPassCityResponseDto getParkingPassCities(Integer userId) throws WayServiceException {
		
		String methodArgs = getExceptionUtil().methodInputArgsAsString(userId);
		
		ParkingPassCityResponseDto parkingPassCities = new ParkingPassCityResponseDto();
		
		Map<String, List<ParkingLots>> parkingLotsMap = new HashMap<>();
		
		List<ParkingPassCityDto> cityAndPakringLots = new ArrayList<>();
		
		try {
			
			ParkingPassCityDto passCityDto = new ParkingPassCityDto();
			passCityDto.setCity("All");
			cityAndPakringLots.add(passCityDto);
			
			List<Object[]> result = parkingPassRepository.fetchParkingPassCities();
			
			if(result != null && result.size() > 0) {
				
				for(Object[] obj : result) {
					
					String city = (String) obj[0];
					
					if(city != null && !city.isEmpty()) {
						
						if(!parkingLotsMap.containsKey(city)) {
							
							List<ParkingLots> cityParkingLot = new ArrayList<>();
							
							ParkingLots parking = new ParkingLots();
							AddressDto address = constructAddressObj(obj,userId);
							
							parking.setListingId((Integer)obj[1]);
							parking.setName((String)obj[2]);
							parking.setAddress(address);
							
							cityParkingLot.add(parking);
							
							parkingLotsMap.put(city, cityParkingLot);
							
						} else {
							
							List<ParkingLots> cityParkingLot = parkingLotsMap.get(city);
							
							ParkingLots parking = new ParkingLots();
							AddressDto address = constructAddressObj(obj,userId);
							
							parking.setListingId((Integer)obj[1]);
							parking.setName((String)obj[2]);
							parking.setAddress(address);
							
							cityParkingLot.add(parking);
							
							parkingLotsMap.put(city, cityParkingLot);
						}
					}
				}
				
				if(parkingLotsMap != null) {
					
					for(Map.Entry<String, List<ParkingLots>> entry : parkingLotsMap.entrySet()) {
						
						ParkingPassCityDto passAndCity = new ParkingPassCityDto();
						
						passAndCity.setCity(entry.getKey());
						passAndCity.setParkinglots(entry.getValue());
						
						cityAndPakringLots.add(passAndCity);
						
					}
				}
			}
			
			parkingPassCities.setParkingPassCities(cityAndPakringLots);
			 
		} catch(Exception ex) {
			handleExceptions(ex, logger, userId , ExceptionModule.PARKING_CONSUMER,
					this.getClass(), "getParkingPassCities", methodArgs);
		}
		
		return parkingPassCities;
	}

	private AddressDto constructAddressObj(Object[] obj, Integer userId) throws WayServiceException {
		
		String methodArgs = getExceptionUtil().methodInputArgsAsString(obj,userId);
		
		AddressDto address = new AddressDto();
		
		try {
			
			address.setAddressLine1((String)obj[3]);
			address.setAddressLine2((String)obj[4]);
			address.setAptNo((String)obj[5]);
			address.setLat((String)obj[6]);
			address.setLon((String)obj[7]);
			address.setLandmark((String)obj[8]);
			address.setState((String)obj[9]);
			address.setStateCode((String)obj[10]);
			address.setCountry((String)obj[11]);
			address.setZipCode((String)obj[12]);
			address.setStreet((String)obj[13]);
			address.setAddressString((String)obj[14]);
			
		} catch(Exception ex) {
			handleExceptions(ex, logger, userId , ExceptionModule.PARKING_CONSUMER,
					this.getClass(), "constructAddressObj", methodArgs);
		}
		
		return address;
	}
	
	@Override
	public List<ParkingPassInfoResponseDto> getParkingPassInfo(Integer userId) throws WayServiceException {
		String methodArgs = getExceptionUtil().methodInputArgsAsString(userId);

		List<ParkingPassInfoResponseDto> parkingPassInfoResponseDtoList = new ArrayList<ParkingPassInfoResponseDto>();
		List<Object[]> parkingPassDetails = parkingPassRepository.fetchParkingPassInfo();

		try {
			if (parkingPassDetails != null && parkingPassDetails.size() > 0) {
				for (Object[] passDetailObject : parkingPassDetails) {
					ParkingPassInfoResponseDto parkingPassInfo = new ParkingPassInfoResponseDto();

					parkingPassInfo.setPassId((Integer) passDetailObject[0]);
					parkingPassInfo.setPassCities(Arrays.asList((String) passDetailObject[1]));
					parkingPassInfo
							.setPassStartTime(DateUtils.convertDateToAnotherTimeZone((String) passDetailObject[2],
									(String) passDetailObject[4], ParkingConsumerConstants.UTCTIMEZONE));
					parkingPassInfo.setPassEndTime(DateUtils.convertDateToAnotherTimeZone((String) passDetailObject[3],
							(String) passDetailObject[4], ParkingConsumerConstants.UTCTIMEZONE));
					parkingPassInfo.setTimezone((String) passDetailObject[4]);

					VehicleDetailsDto vehicleDetail = new VehicleDetailsDto();
					vehicleDetail.setLicensePlateNo((String) passDetailObject[5]);
					vehicleDetail.setCarMake((String) passDetailObject[6]);
					vehicleDetail.setCarModel((String) passDetailObject[7]);
					vehicleDetail.setCarColor((String) passDetailObject[8]);

					parkingPassInfo.setVehicleDetails(vehicleDetail);
					parkingPassInfo.setPricingType((String) passDetailObject[9]);

					String pricingType = (String) passDetailObject[9];
					if (pricingType.equals(ParkingConsumerConstants.DAILY)
							|| pricingType.equals(ParkingConsumerConstants.MONTHLY)) {
						parkingPassInfo.setCreditBasedPass(false);
					} else {
						parkingPassInfo.setCreditBasedPass(true);

					}
					if (parkingPassInfo.isCreditBasedPass()) {
						parkingPassInfo.setTotalCredits(new Byte((byte) passDetailObject[11]).intValue());
						parkingPassInfo.setActivatedCredits(new Byte((byte) passDetailObject[12]).intValue());
						parkingPassInfo.setAvailableCredits(new Byte((byte) passDetailObject[13]).intValue());
						parkingPassInfo.setActivatedCreditLogs(getActivatedCreditLogs(
								passDetailObject[14] != null ? (String) passDetailObject[14] : "", userId));
						parkingPassInfo.setCreditReservations(getCreditReservations(userId));
						parkingPassInfoResponseDtoList.add(parkingPassInfo);

					} else {
						parkingPassInfo.setNonCreditReservations(getNonCreditReservations(userId));
						parkingPassInfoResponseDtoList.add(parkingPassInfo);

					}
				}
			}

		} catch (Exception ex) {
			handleExceptions(ex, logger, userId, ExceptionModule.PARKING_CONSUMER, this.getClass(),
					"getParkingPassInfo", methodArgs);
		}
		return parkingPassInfoResponseDtoList;
	}

	private List<CreditVarientReservationDto> getCreditReservations(Integer userId) throws Exception {
		List<Object[]> creditBasedReservationsList = parkingPassRepository.fetchCreditVarientParkingPassInfo();
		List<CreditVarientReservationDto> creditBasedReservationList = new ArrayList<>();
		List<Object[]> reservationsRemovableList = new ArrayList<Object[]>();
		if (creditBasedReservationsList != null && creditBasedReservationsList.size() > 0) {
			for (int i = 0; i < creditBasedReservationsList.size(); i++) {

				Object[] creditReservation = creditBasedReservationsList.get(i);

				CreditVarientReservationDto creditVarient = new CreditVarientReservationDto();

				String reservationStartDate = DateUtils.convertDateToAnotherTimeZone((String) creditReservation[1],
						(String) creditReservation[21], ParkingConsumerConstants.UTCTIMEZONE);

				String reservationEndDate = DateUtils.convertDateToAnotherTimeZone((String) creditReservation[2],
						(String) creditReservation[21], ParkingConsumerConstants.UTCTIMEZONE);

				creditVarient.setReservationRangeStart(reservationStartDate);
				creditVarient.setReservationRangeEnd(reservationEndDate);
				creditVarient.setCreditLogId((Integer) creditReservation[0]);
				creditVarient.setCanDeactivate(true);
				Integer creditsCount = 0;

				List<Object[]> tempReservationList = new ArrayList<Object[]>();

				for (int j = 0; j < creditBasedReservationsList.size(); j++) {
					Object[] creditReservationObj = creditBasedReservationsList.get(j);
					String reservationStartDateObj = DateUtils.convertDateToAnotherTimeZone(
							(String) creditReservationObj[1], (String) creditReservationObj[21],
							ParkingConsumerConstants.UTCTIMEZONE);

					String reservationEndDateObj = DateUtils.convertDateToAnotherTimeZone(
							(String) creditReservationObj[2], (String) creditReservationObj[21],
							ParkingConsumerConstants.UTCTIMEZONE);

					if (reservationStartDate.equals(reservationStartDateObj)
							&& reservationEndDate.equals(reservationEndDateObj)) {
						tempReservationList.add(creditReservationObj);
						reservationsRemovableList.add(creditReservationObj);
					}

				}
				List<ReservationsDayDto> reservationDaysDtoList = new ArrayList<ReservationsDayDto>();
				for (int l =0 ;l<tempReservationList.size();l++) {
					if(tempReservationList !=null && tempReservationList.size()>0) {
					Object[] tempReservation = tempReservationList.get(l);
					String checkInDate = DateUtils
							.getDateFormat3(DateUtils.convertDateToAnotherTimeZone((String) tempReservation[22],
									(String) tempReservation[21], ParkingConsumerConstants.UTCTIMEZONE));

					ReservationsDayDto reservationDays = new ReservationsDayDto();

					reservationDays.setDate(checkInDate);
					creditsCount += new Byte((byte) tempReservation[3]).intValue();

					List<Object[]> tempReservationDaysList = new ArrayList<Object[]>();
					List<ReservationDto> reservationList = new ArrayList<ReservationDto>();

					for (int k=0;k<tempReservationList.size();) {
						Object[] tempReservationObj = tempReservationList.get(k);
						String checkInDateObj = DateUtils
								.getDateFormat3(DateUtils.convertDateToAnotherTimeZone((String) tempReservationObj[22],
										(String) tempReservationObj[21], ParkingConsumerConstants.UTCTIMEZONE));

						if (checkInDate.equals(checkInDateObj)) {
							ReservationDto reservation = new ReservationDto();							
							setReservationDto(reservation, tempReservationObj, creditVarient, userId);
							reservationList.add(reservation);
							tempReservationDaysList.add(tempReservationObj);
							tempReservationList.remove(tempReservationObj);
							k--;
						}
						k++;	
					}
					
					reservationDays.setReservations(reservationList);
					if (reservationDays.getReservations() == null || reservationDays.getReservations().isEmpty()) {
						creditVarient.setCanDeactivate(true);
					} 				
					reservationDaysDtoList.add(reservationDays);					
					l= -1;
					}
				}
				creditVarient.setCredits(creditsCount);
				creditVarient.setReservationsDays(reservationDaysDtoList);				

				creditBasedReservationList.add(creditVarient);
				creditBasedReservationsList.removeAll(reservationsRemovableList);
				i = -1;
			}
		}
		return creditBasedReservationList;
	}


	private void setReservationDto(ReservationDto reservationDto, Object[] tempReservationObj,
			CreditVarientReservationDto creditVarient, Integer userId) throws WayServiceException, WayDaoException {

		reservationDto.setReservationOGI((String) tempReservationObj[4]);

		VehicleDetailsDto vehicleDetails = new VehicleDetailsDto();
		vehicleDetails.setLicensePlateNo((String) tempReservationObj[5]);
		vehicleDetails.setCarMake((String) tempReservationObj[6]);
		vehicleDetails.setCarModel((String) tempReservationObj[7]);
		vehicleDetails.setCarColor((String) tempReservationObj[8]);

		reservationDto.setVehicleDetails(vehicleDetails);
		reservationDto.setChildListingId((Integer) tempReservationObj[9]);
		reservationDto.setParentListingId((Integer) tempReservationObj[10]);
		reservationDto.setParentListingDesc((String) tempReservationObj[11]);

		AddressDto addressDto = new AddressDto();

		addressDto.setAddressLine1(tempReservationObj[12] != null ? (String) tempReservationObj[12] : null);
		addressDto.setLat(tempReservationObj[13] != null ? (String) tempReservationObj[13] : null);
		addressDto.setLon(tempReservationObj[14] != null ? (String) tempReservationObj[14] : null);
		addressDto.setCity(tempReservationObj[15] != null ? (String) tempReservationObj[15] : null);
		addressDto.setState(tempReservationObj[16] != null ? (String) tempReservationObj[16] : null);
		addressDto.setStateCode(tempReservationObj[17] != null ? (String) tempReservationObj[17] : null);
		addressDto.setCountry(tempReservationObj[18] != null ? (String) tempReservationObj[18] : null);
		addressDto.setZipCode(tempReservationObj[19] != null ? (String) tempReservationObj[19] : null);
		addressDto.setAddressString(tempReservationObj[20] != null ? (String) tempReservationObj[20] : null);

		reservationDto.setParkingLotAddress(addressDto);

		reservationDto.setTimezone((String) tempReservationObj[21]);
		reservationDto.setCheckin(DateUtils.convertDateToAnotherTimeZone((String) tempReservationObj[22],
				(String) tempReservationObj[21], ParkingConsumerConstants.UTCTIMEZONE));
		reservationDto.setCheckout(DateUtils.convertDateToAnotherTimeZone((String) tempReservationObj[23],
				(String) tempReservationObj[21], ParkingConsumerConstants.UTCTIMEZONE));
		reservationDto.setQrCode((String) tempReservationObj[24]);
		reservationDto
				.setReservationStatus(getOrderStatus((String) tempReservationObj[27], (String) tempReservationObj[25],
						(String) tempReservationObj[26], (String) tempReservationObj[21], userId));
		
		if (!(reservationDto.getReservationStatus().equals("cancelled")
				|| reservationDto.getReservationStatus().equals("upcoming"))) {
			creditVarient.setCanDeactivate(false);
		} 

		reservationDto
				.setImages(getImages(tempReservationObj[28] != null ? (String) tempReservationObj[28] : null, userId));
		reservationDto.setOperatingDays(getOperatingDays(tempReservationObj[29], tempReservationObj[30], userId));

		reservationDto.setSmartAccess(
				getSmartAccessLogs(tempReservationObj[31] != null ? (String) tempReservationObj[31] : null, userId));

		LastSuccessfullSmartAccessEventDto lastSuccessFullSmartEvent = new LastSuccessfullSmartAccessEventDto();

		lastSuccessFullSmartEvent.setEventTime(tempReservationObj[32] != null && (String) tempReservationObj[21] != null
				? DateUtils.convertDateToAnotherTimeZone((String) tempReservationObj[32],
						(String) tempReservationObj[21], ParkingConsumerConstants.UTCTIMEZONE)
				: null);
		lastSuccessFullSmartEvent.setEventType((String) tempReservationObj[33]);
		lastSuccessFullSmartEvent.setListingId((Integer) tempReservationObj[34]);
		lastSuccessFullSmartEvent.setOrderIdentifier((String) tempReservationObj[35]);
		lastSuccessFullSmartEvent.setSmartEntryType((String) tempReservationObj[36]);

		reservationDto.setLastSuccessfullSmartAccessEvent(lastSuccessFullSmartEvent);

	}

	private List<NonCreditVarientReservationDto> getNonCreditReservations(Integer userId) throws Exception {
		List<Object[]> nonCreditBasedReservationsList = parkingPassRepository.fetchNonCreditVarientParkingPassInfo();
		List<NonCreditVarientReservationDto> nonCreditBasedReservationList = new ArrayList<>();
		List<ReservationDto> reservationsList = null;
		if (nonCreditBasedReservationsList != null && nonCreditBasedReservationsList.size() > 0) {
			for (Object[] nonCreditReservationsObject : nonCreditBasedReservationsList) {
				reservationsList = new ArrayList<ReservationDto>();
				NonCreditVarientReservationDto nonCreditReservation = new NonCreditVarientReservationDto();

				nonCreditReservation.setDate(DateUtils
						.getDateFormat3(DateUtils.convertDateToAnotherTimeZone((String) nonCreditReservationsObject[18],
								(String) nonCreditReservationsObject[17], ParkingConsumerConstants.UTCTIMEZONE)));

				ReservationDto reservations = new ReservationDto();

				reservations.setReservationOGI((String) nonCreditReservationsObject[0]);

				VehicleDetailsDto vehicleDetails = new VehicleDetailsDto();

				vehicleDetails.setLicensePlateNo((String) nonCreditReservationsObject[1]);
				vehicleDetails.setCarMake((String) nonCreditReservationsObject[2]);
				vehicleDetails.setCarModel((String) nonCreditReservationsObject[3]);
				vehicleDetails.setCarColor((String) nonCreditReservationsObject[4]);

				reservations.setVehicleDetails(vehicleDetails);

				reservations.setChildListingId((Integer) nonCreditReservationsObject[5]);
				reservations.setParentListingId((Integer) nonCreditReservationsObject[6]);
				reservations.setParentListingDesc((String) nonCreditReservationsObject[7]);

				AddressDto addressDto = new AddressDto();

				addressDto.setAddressLine1(
						nonCreditReservationsObject[8] != null ? (String) nonCreditReservationsObject[8] : null);
				addressDto.setLat(
						nonCreditReservationsObject[9] != null ? (String) nonCreditReservationsObject[9] : null);
				addressDto.setLon(
						nonCreditReservationsObject[10] != null ? (String) nonCreditReservationsObject[10] : null);
				addressDto.setCity(
						nonCreditReservationsObject[11] != null ? (String) nonCreditReservationsObject[11] : null);
				addressDto.setState(
						nonCreditReservationsObject[12] != null ? (String) nonCreditReservationsObject[12] : null);
				addressDto.setStateCode(
						nonCreditReservationsObject[13] != null ? (String) nonCreditReservationsObject[13] : null);
				addressDto.setCountry(
						nonCreditReservationsObject[14] != null ? (String) nonCreditReservationsObject[14] : null);
				addressDto.setZipCode(
						nonCreditReservationsObject[15] != null ? (String) nonCreditReservationsObject[15] : null);
				addressDto.setAddressString(
						nonCreditReservationsObject[16] != null ? (String) nonCreditReservationsObject[16] : null);

				reservations.setParkingLotAddress(addressDto);
				reservations.setTimezone((String) nonCreditReservationsObject[17]);
				reservations.setCheckin(DateUtils.convertDateToAnotherTimeZone((String) nonCreditReservationsObject[18],
						(String) nonCreditReservationsObject[17], ParkingConsumerConstants.UTCTIMEZONE));
				reservations
						.setCheckout(DateUtils.convertDateToAnotherTimeZone((String) nonCreditReservationsObject[19],
								(String) nonCreditReservationsObject[17], ParkingConsumerConstants.UTCTIMEZONE));
				reservations.setQrCode((String) nonCreditReservationsObject[20]);
				reservations.setReservationStatus(getOrderStatus((String) nonCreditReservationsObject[23],
						(String) nonCreditReservationsObject[21], (String) nonCreditReservationsObject[22],
						(String) nonCreditReservationsObject[19], userId));
				reservations.setImages(getImages((String) nonCreditReservationsObject[24], userId));
				reservations.setOperatingDays(
						getOperatingDays(nonCreditReservationsObject[25], nonCreditReservationsObject[26], userId));
				reservations.setSmartAccess(getSmartAccessLogs(
						nonCreditReservationsObject[27] != null ? (String) nonCreditReservationsObject[27] : null,
						userId));

				LastSuccessfullSmartAccessEventDto lastSuccessFullSmartEvent = new LastSuccessfullSmartAccessEventDto();

				lastSuccessFullSmartEvent.setEventTime((String) nonCreditReservationsObject[28] != null
						&& (String) nonCreditReservationsObject[17] != null
								? DateUtils.convertDateToAnotherTimeZone((String) nonCreditReservationsObject[28],
										(String) nonCreditReservationsObject[17], ParkingConsumerConstants.UTCTIMEZONE)
								: null);
				lastSuccessFullSmartEvent.setEventType((String) nonCreditReservationsObject[29]);
				lastSuccessFullSmartEvent.setListingId((Integer) nonCreditReservationsObject[30]);
				lastSuccessFullSmartEvent.setOrderIdentifier((String) nonCreditReservationsObject[31]);
				lastSuccessFullSmartEvent.setSmartEntryType((String) nonCreditReservationsObject[32]);

				reservations.setLastSuccessfullSmartAccessEvent(
						lastSuccessFullSmartEvent != null ? lastSuccessFullSmartEvent : null);

				reservationsList.add(reservations);
				nonCreditReservation.setReservations(reservationsList);

				nonCreditBasedReservationList.add(nonCreditReservation);

			}
			return nonCreditBasedReservationList;
		}
		return null;
	}

	private List<ActivatedCreditLogDto> getActivatedCreditLogs(String activatedCreditLogStr, Integer userId)
			throws WayServiceException {

		String methodArgs = getExceptionUtil().methodInputArgsAsString(activatedCreditLogStr);

		List<ActivatedCreditLogDto> activatedCreditLog = new ArrayList<>();

		try {
			if (activatedCreditLogStr != null) {
				String[] activatedCreditLogArr = activatedCreditLogStr.split("\\~");
				if (activatedCreditLogArr != null) {
					for (String activatedCreditLogData : activatedCreditLogArr) {

						String[] creditLogArr = activatedCreditLogData.split("#");

						ActivatedCreditLogDto activatedCreditLogDto = new ActivatedCreditLogDto();
						String creditLogId = (String) creditLogArr[0];
						String creditsActivated = (String) creditLogArr[1];
						String creditStartTime = (String) creditLogArr[2];
						String creditEndTime = (String) creditLogArr[3];

						activatedCreditLogDto.setCreditLogId(Integer.valueOf(creditLogId));
						activatedCreditLogDto.setCreditsActivated(Integer.valueOf(creditsActivated));
						activatedCreditLogDto.setCreditStartTime(creditStartTime);
						activatedCreditLogDto.setCreditEndTime(creditEndTime);

						activatedCreditLog.add(activatedCreditLogDto);

					}
				}

			}
		} catch (Exception ex) {
			handleExceptions(ex, logger, userId, ExceptionModule.PARKING_CONSUMER, this.getClass(), "getImages",
					methodArgs);
		}

		return activatedCreditLog;
	}

	private List<ImageDto> getImages(String imagesStr, Integer userId) throws WayServiceException {

		String methodArgs = getExceptionUtil().methodInputArgsAsString(imagesStr);

		List<ImageDto> parkingImages = new ArrayList<>();
		try {
			if (imagesStr != null) {

				String[] imagesArr = imagesStr.split("\\~");

				if (imagesArr != null) {
					for (String imgData : imagesArr) {

						String[] imgArr = imgData.split("\\^");

						ImageDto imageDto = new ImageDto();
						String imagePath = (String) imgArr[0];
						String thumbPath = (String) imgArr[1];
						String isDefault = (String) imgArr[2];

						imageDto.setImagePath(DocumentType.LISTING_IMAGE.getDocumentType() + "/" + imagePath);
						imageDto.setThumbImagePath(DocumentType.LISTING_THUMBNAIL.getDocumentType() + "/" + thumbPath);
						imageDto.setIsDefault(isDefault);

						parkingImages.add(imageDto);

					}
				}

			}
		} catch (Exception ex) {
			handleExceptions(ex, logger, userId, ExceptionModule.PARKING_CONSUMER, this.getClass(), "getImages",
					methodArgs);
		}

		return parkingImages;
	}

	private List<OperatingDay> getOperatingDays(Object operatingDaysObj, Object operatingHoursObj, Integer userId)
			throws WayServiceException {

		String methodArgs = getExceptionUtil().methodInputArgsAsString(operatingDaysObj, operatingHoursObj);

		List<OperatingDay> operatingDays = new ArrayList<>();

		try {

			if (operatingDaysObj != null) {

				String[] operatingDaysArr = ((String) operatingDaysObj).split(",");

				String[] operatingHoursArr = null;

				if (operatingHoursObj != null) {
					operatingHoursArr = ((String) operatingHoursObj).split("\\$");
				}

				int index = 0;
				for (String day : operatingDaysArr) {

					String[] dayArr = day.split("-");

					OperatingDay opDay = new OperatingDay();
					opDay.setDay(Day.valueOf(dayArr[0]));
					opDay.setDayStatus(dayArr[1]);

					String operatingHours = operatingHoursArr[index];

					if (!operatingHours.equalsIgnoreCase("null")) {
						List<OperatingHours> operatingHoursList = new ArrayList<>();

						if (operatingHours != null) {
							String[] timeArr = operatingHours.split(",");
							if (timeArr != null) {
								for (String times : timeArr) {
									OperatingHours opHours = new OperatingHours();

									String[] fromToTimes = times.split("-");
									opHours.setFrom(fromToTimes[0]);
									opHours.setTo(fromToTimes[1]);
									opHours.setOperatingHourStatus(fromToTimes[2]);

									operatingHoursList.add(opHours);
								}
							}

						}
						opDay.setOperatingHours(operatingHoursList);

					}

					operatingDays.add(opDay);
					index++;
				}
			}
		} catch (Exception ex) {
			handleExceptions(ex, logger, userId, ExceptionModule.PARKING_CONSUMER, this.getClass(), "setOperatingDays",
					methodArgs);
		}

		return operatingDays;
	}

	private List<SmartAccessDto> getSmartAccessLogs(String getSmartAccessInfo, Integer userId)
			throws WayServiceException {

		String methodArgs = getExceptionUtil().methodInputArgsAsString(getSmartAccessInfo);

		List<SmartAccessDto> smartAcessLogList = new ArrayList<>();

		try {
			if (getSmartAccessInfo != null) {
				String[] smartAccessArr = getSmartAccessInfo.split("\\^");
				if (smartAccessArr != null) {
					for (String activatedCreditLogData : smartAccessArr) {

						String[] smartAcessInfo = activatedCreditLogData.split("\\~");
						SmartAccessDto smartAccess = new SmartAccessDto();

						String smartGateAccessID = (String) smartAcessInfo[0];
						String gateType = (String) smartAcessInfo[1];
						String entryType = (String) smartAcessInfo[2];
						String gateIdentifier = (String) smartAcessInfo[3];
						String lat = (String) smartAcessInfo[4];
						String lon = (String) smartAcessInfo[5];
						String ticketingAvailable = (String) smartAcessInfo[6];

						smartAccess.setSmartGateAccessID(Integer.valueOf(smartGateAccessID));
						smartAccess.setGateType(gateType);
						smartAccess.setEntryType(entryType);
						smartAccess.setGateIdentifier(gateIdentifier);
						smartAccess.setLat(lat);
						smartAccess.setLon(lon);
						smartAccess.setTicketingAvailable(ticketingAvailable);
						smartAcessLogList.add(smartAccess);

					}
				}
			}
		} catch (Exception ex) {
			handleExceptions(ex, logger, userId, ExceptionModule.PARKING_CONSUMER, this.getClass(), "getImages",
					methodArgs);
		}

		return smartAcessLogList;
	}

	private String getOrderStatus(String orderStatus, String actualStartDateTime, String actualEndDateTime,
			String checkOut, Integer userId) throws WayDaoException {

		Date scheduledExitTime = DateUtils.getDateFromString(checkOut);
		String reservationStatus = "";

		if (orderStatus.equalsIgnoreCase("cancelled")) {

			reservationStatus = "cancelled";
		}
		if (actualEndDateTime != null || orderStatus.equalsIgnoreCase("Completed")) {

			reservationStatus = "completed";

		}
		if (orderStatus.equalsIgnoreCase("Confirmed") && actualStartDateTime == null) {

			reservationStatus = "upcoming";

		}
		if (orderStatus.equalsIgnoreCase("Confirmed") && actualStartDateTime != null && actualEndDateTime == null) {

			reservationStatus = "ongoing";

		}
		if (orderStatus.equalsIgnoreCase("Confirmed") && actualStartDateTime == null
				&& (scheduledExitTime.compareTo(new Date()) < 0)) {

			reservationStatus = "expired";

		}

		return reservationStatus;
	}
	
	

}