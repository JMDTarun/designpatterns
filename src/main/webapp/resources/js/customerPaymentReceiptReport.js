$(function() {

	$.extend($.jgrid.defaults, {
		datatype: 'json',
		jsonReader : {
			repeatitems:false,
			total: function(result) {
				//Total number of pages
				return Math.ceil(result.total / result.max);
			},
			records: function(result) {
				//Total number of records
				return result.total;
			}
		},
		prmNames: {
			page: "page",
			rows: "size",
			sort: "sort"
		},
		sortname: 'customer.customerCode',
		sortorder: 'asc',
		height: 'auto',
		forceFit: true,
        autowidth: true,
		viewrecords: true,
		rowList: [10, 20, 50, 100],
		altRows: true,
		loadError: function(xhr, status, error) {
			alert(error);
		}
	});
	
	var URL = '/paymentReceipt';
	var options = {
		url: URL,
		editurl: URL,
		height: 'auto',
        forceFit: true,
        autowidth: true,
        shrinkToFit: true,
        rowNum: 10,
		colModel:[
			{
				name:'customer.name',
				label: 'Customer Name',
				index: 'customer.name'
			},
			{
				name:'customer.customerCode',
				label: 'Customer Code',
				index: 'customer.customerCode'
			},
			{
				name:'customer.area.name',
				label: 'Area',
				index: 'customer.area.name'
			},
			{
				name:'customer.street.streetNumber',
				label: 'Street',
				index: 'customer.street.streetNumber'
			},
			{
				name:'customer.subArea.wardNumber',
				label: 'Sub Area',
				index: 'customer.subArea.wardNumber'
			},
			{
				name:'amountCredit',
				label: 'Amount',
				index: 'amountCredit'
			},
			{
				name:'chequeNumber',
				label: 'Cheque Number',
				index: 'chequeNumber'
			},
			{
				name:'chequeDate',
				label: 'Cheque Date',
				index: 'chequeDate',
				formatter:'date',
				formatoptions: { newformat: 'Y/m/d'}
			},
			{
				name:'paymentDate',
				label: 'Payment Date',
				index: 'paymentDate',
				formatter:'date',
				formatoptions: { newformat: 'Y/m/d'}
			},
			{
				name:'paymentMode',
				label: 'Payment Mode',
				index: 'paymentMode'
			},
			{
				name:'paymentType',
				label: 'Payment Type',
				index: 'paymentType'
			}
		],
		caption: "Customer Payment Receipt",
		pager : '#pagerCustomerPaymentReceiptReport',
		height: 'auto',
	};

	$("#customerPaymentReceiptReport")
			.jqGrid(options)
			.navGrid('#pagerCustomerPaymentReceiptReport',
					{edit:false,add:false,del:false, search:false}
	);

	$("#customerPaymentReceiptReport").navButtonAdd("#pagerCustomerPaymentReceiptReport",
            {
                buttonicon: "ui-icon-document",
                title: "Download",
                id: "downloadReport",
                caption: "Download",
                position: "last",
                onClickButton: function() {
                	$.ajax({url: "downloadPaymentReceiptReport", success: function(result){
                		
                	  }});
                }
            });
	
	$.ajax({url: "getAllAreas", success: function(result){
		$("#selectArea").addClass("ui-widget ui-jqdialog");
		$("#selectArea").select2();
		for (var key in result) {
	    	$("#selectArea").append('<option value="'+key+'">'+result[key]+'</option>')
	    }
	  }});
	
	$.ajax({url: "getAllSubAreas", success: function(result){
		$("#selectSubArea").addClass("ui-widget ui-jqdialog");
		$("#selectSubArea").select2();
		for (var key in result) {
	    	$("#selectSubArea").append('<option value="'+key+'">'+result[key]+'</option>')
	    }
	  }});
	
	$.ajax({url: "getAllStreets", success: function(result){
		$("#selectStreet").addClass("ui-widget ui-jqdialog");
		$("#selectStreet").select2();
		for (var key in result) {
	    	$("#selectStreet").append('<option value="'+key+'">'+result[key]+'</option>')
	    }
	  }});
	
	$("#selectPaymentMode").addClass("ui-widget ui-jqdialog");
	$("#selectPaymentMode").select2();
	
	$("#selectPaymentType").addClass("ui-widget ui-jqdialog");
	$("#selectPaymentType").select2();
	
	$("#startDate").datepicker({ defaultDate: new Date() });
	$("#endDate").datepicker({ defaultDate: new Date() });
	
	$("#downloadAnchor").click(function(){
		var urlStr = 'downloadPaymentReceiptReport?'+getUrlParams();
		$(this).attr("href", urlStr);
    });
		
	$("#submitFilters").click(function() {
        var urlStr = 'paymentReceipt?'+getUrlParams();
    	$("#customerPaymentReceiptReport").setGridParam({
	      url:urlStr,
	      page:1
    	}).trigger("reloadGrid");
    });
	
	function getUrlParams() {
		var urlStr = '';
        if($("#selectArea").val() !== "") {
        	urlStr += encodeURIComponent("prAreaId") + '=' + encodeURIComponent($("#selectArea").val()) + "&";
        }
        if($("#selectSubArea").val() !== "") {
        	urlStr += encodeURIComponent("prSubAreaId") + '=' + encodeURIComponent($("#selectSubArea").val()) + "&";
        }
        if($("#selectStreet").val() !== "") {
        	urlStr += encodeURIComponent("prStreetId") + '=' + encodeURIComponent($("#selectStreet").val()) + "&";
        }
        if($("#machineId").val() !== "") {
        	urlStr += encodeURIComponent("prMachineNumner") + '=' + encodeURIComponent($("#machineId").val()) + "&";
        }
        if($("#startDate").val() !== "") {
        	urlStr += encodeURIComponent("prFromDate") + '=' + encodeURIComponent($("#startDate").val()) + "&";
        }
        if($("#endDate").val() !== "") {
        	urlStr += encodeURIComponent("prToDate") + '=' + encodeURIComponent($("#endDate").val()) + "&";
        }
        if($("#selectPaymentMode").val() !== "") {
        	urlStr += encodeURIComponent("prPaymentMode") + '=' + encodeURIComponent($("#selectPaymentMode").val()) + "&";
        }
        if($("#selectPaymentType").val() !== "") {
        	urlStr += encodeURIComponent("prPaymentType") + '=' + encodeURIComponent($("#selectPaymentType").val()) + "&";
        }
        return urlStr;
	}
	
});

