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
	
	var URL = '/customerPartialPaymentReport';
	var options = {
		url: URL,
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
				name:'customer.address',
				label: 'Address',
				index: 'customer.address'
			},
			{
				name:'customer.mobile',
				label: 'Mobile',
				index: 'customer.mobile'
			},
			{
				name:'customer.monthlyTotal',
				label: 'Monthly Total',
				index: 'customer.monthlyTotal'
			},
			{
				name:'creditOrDebit',
				label: 'Credit/Debit',
				index: 'creditOrDebit'
			},
			{
				name:'balance',
				label: 'Balance',
				index: 'balance'
			}
		],
		caption: "Customer Partial Payment",
		pager : '#pagerCustomerPartialPaymentReport',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#successDiv").hide();
	$("#errorDiv").hide();
	
	$("#customerPartialPaymentReport")
			.jqGrid(options)
			.navGrid('#pagerCustomerPartialPaymentReport',
					{edit:false,add:false,del:false, search:false}
	);

	var d = new Date();
	d.setMonth(d.getMonth() - 1);
	
	$("#partialPaymanetBetweenStart").datepicker({ defaultDate: d });
	$("#partialPaymanetBetweenEnd").datepicker({ defaultDate: new Date() });
	
	$("#partialPaymanetBetweenStart").datepicker('setDate', d);
	$("#partialPaymanetBetweenEnd").datepicker('setDate', new Date());
	
	$("#selectOutstanding").addClass("ui-widget ui-jqdialog");
	$("#selectOutstanding").select2();
	
	$("#deactivateSetTopBoxes").click(function() {
		$("#deactivateSetTopBoxes").attr("disabled", "disabled");
		$.ajax({url: "deactivateCustomerNoPaymentSetTopBox?"+getUrlParams(), success: function(result){
			$("#successDiv").text("Successfully deactivated Set top boxes.");
			$("#successDiv").show();
			setTimeout(function() {
		        $("#successDiv").hide('blind', {}, 500)
		    }, 5000);
			$("#errorDiv").hide();
		}});
	});
	
	$("#downloadAnchor").click(function(){
		var urlStr = 'downloadCustomerPartialPaymentReport?'+getUrlParams();
		$(this).attr("href", urlStr);
    });
		
	$("#submitFilters").click(function() {
        var urlStr = 'customerPartialPaymentReport?'+getUrlParams();
    	$("#customerPartialPaymentReport").setGridParam({
	      url:urlStr,
	      page:1
    	}).trigger("reloadGrid");
    });
	
	function getUrlParams() {
		var urlStr = '';
        if($("#partialPaymanetBetweenStart").val() !== "") {
        	urlStr += encodeURIComponent("startDate") + '=' + encodeURIComponent($("#partialPaymanetBetweenStart").val()) + "&";
        }
        if($("#partialPaymanetBetweenEnd").val() !== "") {
        	urlStr += encodeURIComponent("endDate") + '=' + encodeURIComponent($("#partialPaymanetBetweenEnd").val()) + "&";
        }
        if($('input[name=paymentBetween]:checked').val() === "noPayment") {
        	urlStr += encodeURIComponent("noPaymentBetween") + '= true' + "&";
        }
        if($("#selectOutstanding").val() !== "") {
        	urlStr += encodeURIComponent("outstandingValue") + '=' + encodeURIComponent($("#selectOutstanding").val()) + "&";
        }
        return urlStr;
	}
});

