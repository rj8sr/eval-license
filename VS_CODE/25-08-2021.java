		List<AccountActivityDetailsResponse> securityOrderDetailsList = new ArrayList<>();
		AccountActivityDetailsResponse securityOrderDetail = new AccountActivityDetailsResponse();
		securityOrderDetail.setId("SCO_OrderID");
		securityOrderDetail.setClientOrderId("SCO_Client_Order_ID");
		securityOrderDetail.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd").parse("2021-05-29"));
		securityOrderDetail.setUpdatedAt(new SimpleDateFormat("yyyy-MM-dd").parse("2021-05-29"));
		securityOrderDetail.setSubmittedAt(new SimpleDateFormat("yyyy-MM-dd").parse("2021-05-29"));
		securityOrderDetail.setSettleDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-05-29"));
		securityOrderDetail.setAssetId("SCO_Asset_ID");
		securityOrderDetail.setSymbol("SCO_Symbol");
		securityOrderDetail.setQuantity("SCO_Quantity");
		securityOrderDetail.setFilledQuantity("SCO_Filled_Quantity");
		securityOrderDetail.setFilledAveragePrice("SCO_Filled_Average_Price");
		securityOrderDetail.setType("SCO_Type");
		securityOrderDetail.setLimitPrice("SCO_Limit_Price");
		securityOrderDetail.setSide("SCO_Side");
		securityOrderDetail.setTimeInForce("SCO_Time_In_Force");
		securityOrderDetail.setStatus("SCO_Status");
		securityOrderDetail.setEntryType("SCO_Entry_Type");
		securityOrderDetail.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-05-29"));
		securityOrderDetailsList.add(securityOrderDetail);

		List<AccountActivityDetailsResponse> journalOrderDetailsList = new ArrayList<>();
		AccountActivityDetailsResponse journalOrderDetails = new AccountActivityDetailsResponse();
		journalOrderDetails.setId("JUO_JournalID");
		journalOrderDetails.setEntryType("JUO_Entry_Type");
		journalOrderDetails.setSymbol("JUO_Symbol");
		journalOrderDetails.setQuantity("JUO_Quantity");
		journalOrderDetails.setPrice("JUO_Price");
		journalOrderDetails.setStatus("JUO_Status");
		journalOrderDetails.setDescription("JUO_Description");
		journalOrderDetails.setSettleDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-05-29"));
		journalOrderDetails.setNetAmount("JUO_Net_Amount");
		journalOrderDetails.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-05-29"));
		journalOrderDetailsList.add(journalOrderDetails);

		StockRewardsRepository dbCall = mock(StockRewardsRepository.class);

		String accountId = dbCall.fetchAccountIdByUserId(5);
		dbCall.fetchJournalOrderDetailsByAccountId(1, 2, 3, accountId);
		dbCall.fetchSecurityOrderDetailsByUserId(1, 2, 3, 5);

		when(Stream.of(securityOrderDetailsList, journalOrderDetailsList).flatMap(Collection::stream)
				.sorted(Comparator.comparing(AccountActivityDetailsResponse::getCreatedDate).reversed())
				.collect(Collectors.toList()));
		//PaginationUtil.getPaginationDataObj(list, 1, 2, list.size());

		PaginationData<AccountActivityDetailsResponse> activityDetail = tradingService.getAccountActivityDetails(1, 2,
				3, 5);