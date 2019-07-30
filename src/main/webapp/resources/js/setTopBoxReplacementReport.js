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
	
	var URL = '/setTopBoxReplacementReport';
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
				name:'replacedForCustomer.name',
				label: 'Customer Name',
				index: 'replacedForCustomer.name'
			},
			{
				name:'replacedForCustomer.customerCode',
				label: 'Customer Code',
				index: 'replacedForCustomer.customerCode'
			},
			{
				name:'replacedForCustomer.area.name',
				label: 'Area',
				index: 'replacedForCustomer.area.name'
			},
			{
				name:'replacedForCustomer.street.streetNumber',
				label: 'Street',
				index: 'replacedForCustomer.street.streetNumber'
			},
			{
				name:'replacedForCustomer.subArea.wardNumber',
				label: 'Sub Area',
				index: 'replacedForCustomer.subArea.wardNumber'
			},
			{
				name:'oldSetTopBox.setTopBoxNumber',
				label: 'Old Set Top Box',
				index: 'oldSetTopBox.setTopBoxNumber'
			},
			{
				name:'replacedSetTopBox.setTopBoxNumber',
				label: 'Replaced Set Top Box',
				index: 'replacedSetTopBox.setTopBoxNumber'
			},
			{
				name:'replacementType',
				label: 'Replaced Type',
				index: 'replacementType'
			},
			{
				name:'replacementReason',
				label: 'Replaced Reason',
				index: 'replacementReason'
			},
			{
				name:'replacementCharge',
				label: 'Replaced Charge',
				index: 'replacementCharge'
			}
		],
		caption: "Customer Outstanding",
		pager : '#pagerSetTopBoxReplacementReport',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#setTopBoxReplacementReport")
			.jqGrid(options)
			.navGrid('#pagerSetTopBoxReplacementReport',
					{edit:false,add:false,del:false, search:false}
	);
	
	$.ajax({url: "getAllCustomers", success: function(result){
		$("#selectCustomerCode").addClass("ui-widget ui-jqdialog");
		$("#selectCustomerCode").select2();
		for (var key in result) {
	    	$("#selectCustomerCode").append('<option value="'+key+'">'+result[key].customerCode+'</option>')
	    }
	  }});
	
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
	
	$("#selectReplacementReason").addClass("ui-widget ui-jqdialog");
	$("#selectReplacementReason").select2();
	
	$("#selectReplacementType").addClass("ui-widget ui-jqdialog");
	$("#selectReplacementType").select2();
	
	$("#downloadAnchor").click(function(){
		var urlStr = 'downloadSetTopBoxReplacementReport?'+getUrlParams();
		$(this).attr("href", urlStr);
    });
		
	$("#submitFilters").click(function() {
        var urlStr = 'setTopBoxReplacementReport?'+getUrlParams();
    	$("#setTopBoxReplacementReport").setGridParam({
	      url:urlStr,
	      page:1
    	}).trigger("reloadGrid");
    });
	
	function getUrlParams() {
		var urlStr = '';
		if($("#selectCustomerCode").val() !== "") {
        	urlStr += encodeURIComponent("customerId") + '=' + encodeURIComponent($("#selectCustomerCode").val()) + "&";
        }
        if($("#selectArea").val() !== "") {
        	urlStr += encodeURIComponent("areaId") + '=' + encodeURIComponent($("#selectArea").val()) + "&";
        }
        if($("#selectSubArea").val() !== "") {
        	urlStr += encodeURIComponent("subAreaId") + '=' + encodeURIComponent($("#selectSubArea").val()) + "&";
        }
        if($("#selectStreet").val() !== "") {
        	urlStr += encodeURIComponent("streetId") + '=' + encodeURIComponent($("#selectStreet").val()) + "&";
        }
        if($("#selectReplacementReason").val() !== "") {
        	urlStr += encodeURIComponent("replacementReason") + '=' + encodeURIComponent($("#selectReplacementReason").val()) + "&";
        }
        if($("#selectReplacementType").val() !== "") {
        	urlStr += encodeURIComponent("replacementType") + '=' + encodeURIComponent($("#selectReplacementType").val()) + "&";
        }
        if($("#chargeStart").val() !== "") {
        	urlStr += encodeURIComponent("replacementAmountStart") + '=' + encodeURIComponent($("#chargeStart").val()) + "&";
        }
        if($("#chargeEnd").val() !== "") {
        	urlStr += encodeURIComponent("replacementAmountEnd") + '=' + encodeURIComponent($("#chargeEnd").val()) + "&";
        }
        return urlStr;
	}
	
});

