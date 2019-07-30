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
	
	var URL = '/setTopBoxActiveDeactiveReport';
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
				name:'customerName',
				label: 'Customer Name',
				index: 'customerName'
			},
			{
				name:'customerCode',
				label: 'Customer Code',
				index: 'customerCode'
			},
			{
				name:'area',
				label: 'Area',
				index: 'area'
			},
			{
				name:'subArea',
				label: 'Sub Area',
				index: 'subArea'
			},
			{
				name:'street',
				label: 'Street',
				index: 'street'
			},
			{
				name:'dateTime',
				label: 'Date Time',
				index: 'dateTime',
				formatter:'date',
				formatoptions: { newformat: 'Y/m/d'}
			},
			{
				name:'setTopBoxStatus',
				label: 'Set Top Box Status',
				index: 'setTopBoxStatus'
			}
		],
		caption: "Customer Outstanding",
		pager : '#pagerSetTopBoxActiveDeactiveReport',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#setTopBoxActiveDeactiveReport")
			.jqGrid(options)
			.navGrid('#pagerSetTopBoxActiveDeactiveReport',
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
	
	$("#selectSetTopBoxStatus").addClass("ui-widget ui-jqdialog");
	$("#selectSetTopBoxStatus").select2();
	
	$("#startDate").datepicker({ defaultDate: new Date() });
	$("#endDate").datepicker({ defaultDate: new Date() });
	
	$("#downloadAnchor").click(function(){
		var urlStr = 'downloadSetTopBoxActiveDeactiveReport?'+getUrlParams();
		$(this).attr("href", urlStr);
    });
		
	$("#submitFilters").click(function() {
        var urlStr = 'setTopBoxActiveDeactiveReport?'+getUrlParams();
    	$("#setTopBoxActiveDeactiveReport").setGridParam({
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
        if($("#selectSetTopBoxStatus").val() !== "") {
        	urlStr += encodeURIComponent("setTopBoxStatus") + '=' + encodeURIComponent($("#selectSetTopBoxStatus").val()) + "&";
        }
        if($("#startDate").val() !== "") {
        	urlStr += encodeURIComponent("startDate") + '=' + encodeURIComponent($("#startDate").val()) + "&";
        }
        if($("#endDate").val() !== "") {
        	urlStr += encodeURIComponent("endDate") + '=' + encodeURIComponent($("#endDate").val()) + "&";
        }
        return urlStr;
	}
	
});

