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
				height: 'auto',
				forceFit: true,
		        autowidth: true,
				viewrecords: true,
				altRows: true,
				loadError: function(xhr, status, error) {
					alert(error);
				}
			});

	var URL = '/customerLedgreReport';
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
				name:'date',
				label: 'Date',
				index: 'date',
				formatter:'date',
				formatoptions: { newformat: 'Y/m/d'}
			},
			{
				name:'action',
				label: 'Action',
				index: 'action'
			},
			{
				name:'action',
				label: 'Action',
				index: 'action'
			},
			{
				name:'setTopBoxNumber',
				label: 'Set Top Box Number',
				index: 'setTopBoxNumber'
			},
			{
				name:'credit',
				label: 'Credit',
				index: 'credit'
			},
			{
				name:'debit',
				label: 'Debit',
				index: 'debit'
			},
			{
				name:'creditOrDebit',
				label: 'Type',
				index: 'creditOrDebit'
			},
			{
				name:'balance',
				label: 'Balance',
				index: 'balance'
			}
		],
		caption: "Customer Ledgre",
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#customerLedgreReport")
			.jqGrid(options);

	$.ajax({url: "getAllCustomers", success: function(result){
		$("#selectCustomerCode").addClass("ui-widget ui-jqdialog");
		$("#selectCustomerCode").select2();
		for (var key in result) {
	    	$("#selectCustomerCode").append('<option value="'+key+'">'+result[key].customerCode+'</option>')
	    }
		
		$("#selectCustomerName").addClass("ui-widget ui-jqdialog");
		$("#selectCustomerName").select2();
		for (var key in result) {
	    	$("#selectCustomerName").append('<option value="'+key+'">'+result[key].name+'</option>')
	    }
	  }});

	$("#selectCustomerCode").change(function() {
		$("#selectCustomerName").val($(this).val()).change();
	});
	
	$("#downloadAnchor").click(function(){
		var urlStr = 'downloadCustomerLedgreReport?'+getUrlParams();
		$(this).attr("href", urlStr);
    });
		
	$("#submitFilters").click(function(){
        var urlStr = 'customerLedgreReport?'+getUrlParams();
    	$("#customerLedgreReport").setGridParam({
	      url:urlStr,
	      page:1
    	}).trigger("reloadGrid");
    });
	
	function getUrlParams() {
		var urlStr = '';
        if($("#selectCustomerCode").val() !== "") {
        	urlStr += encodeURIComponent("customerId") + '=' + encodeURIComponent($("#selectCustomerCode").val()) + "&";
        } else if($("#selectCustomerName").val() !== "") {
        	urlStr += encodeURIComponent("customerId") + '=' + encodeURIComponent($("#selectCustomerName").val()) + "&";
        }
        return urlStr;
	}
	
});

