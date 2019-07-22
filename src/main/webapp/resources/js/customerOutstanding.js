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
				sortname: 'customer.id',
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

	$.extend($.jgrid.edit, {
				closeAfterEdit: true,
				closeAfterAdd: false,
				ajaxEditOptions: { contentType: "application/x-www-form-urlencoded" },
				mtype: 'POST',
				serializeEditData: function(data) {
					var url = Object.keys(data).map(function(k) {
					    return encodeURIComponent(k) + '=' + encodeURIComponent(data[k].replace("_empty", ""))
					}).join('&');
					return url;
					//delete data.oper;
					//return JSON.stringify(data);
				},
				errorTextFormat: function (response) {
					if(response.responseText) {
						var obj = JSON.parse(response.responseText);
						if(obj.errorCode && obj.errorCode != null) {
							return obj.errorCode+" "+ obj.errorCause;
						}
					}
				    return "Data Saved!";
				}
			});
	$.extend($.jgrid.del, {
				mtype: 'DELETE',
				serializeDelData: function() {
					return "";
				}
			});

	var editOptions = {
			width: 700,
		onclickSubmit: function(params, postdata) {
			params.url = 'customerType/' + postdata.id;
		}
	};
	var addOptions = {
			width: 700,
		onclickSubmit: function(params, postdata) {
			params.url = 'customerType';
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'customerType/' + postdata;
		}
	};

	var URL = '/customerReport';
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
				name:'customer.mobile',
				label: 'Mobile',
				index: 'customer.mobile'
			},
			{
				name:'customer.balance',
				label: 'Customer Outstanding',
				index: 'customer.balance'
			}
		],
		caption: "Customer Outstanding",
		pager : '#pagerCustomerOutstanding',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#customerReport")
			.jqGrid(options)
			.navGrid('#pagerCustomerOutstanding',
					{edit:false,add:false,del:false, search:false}
	);

	$("#customerOutstanding").navButtonAdd("#pagerCustomerOutstanding",
            {
                buttonicon: "ui-icon-document",
                title: "Download",
                id: "downloadReport",
                caption: "Download",
                position: "last",
                onClickButton: function() {
                	$.ajax({url: "downloadCustomerReport", success: function(result){
                		
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
	
	$.ajax({url: "getAllPacks", success: function(result){
		$("#selectPack").addClass("ui-widget ui-jqdialog");
		$("#selectPack").select2();
		for (var key in result) {
	    	$("#selectPack").append('<option value="'+key+'">'+result[key].name+'</option>');
	    }
	  }});
	
	$.ajax({url: "getDistinctPackPrices", success: function(result){
		$("#selectRent").addClass("ui-widget ui-jqdialog");
		$("#selectRent").select2();
		console.log("1111111 "+result);
		result.forEach(function (item) {
			$("#selectRent").append('<option value="'+item+'">'+item+'</option>');
		});
	  }});
	
	$("#selectCustomerStatus").addClass("ui-widget ui-jqdialog");
	$("#selectCustomerStatus").select2();
	
	$("#downloadAnchor").click(function(){
		var urlStr = 'downloadCustomerReport?'+getUrlParams();
		$(this).attr("href", urlStr);
    });
		
	$("#submitFilters").click(function(){
        var urlStr = 'customerReport?'+getUrlParams();
    	$("#customerReport").setGridParam({
	      url:urlStr,
	      page:1
    	}).trigger("reloadGrid");
    });
	
	function getUrlParams() {
		var urlStr = '';
        if($("#selectArea").val() !== "") {
        	urlStr += encodeURIComponent("areaId") + '=' + encodeURIComponent($("#selectArea").val()) + "&";
        }
        if($("#selectSubArea").val() !== "") {
        	urlStr += encodeURIComponent("subAreaId") + '=' + encodeURIComponent($("#selectSubArea").val()) + "&";
        }
        if($("#selectStreet").val() !== "") {
        	urlStr += encodeURIComponent("streetId") + '=' + encodeURIComponent($("#selectStreet").val()) + "&";
        }
        if($("#selectCustomerStatus").val() !== "") {
        	urlStr += encodeURIComponent("customerStatus") + '=' + encodeURIComponent($("#selectCustomerStatus").val()) + "&";
        }
        if($("#totalCharge").val() !== "") {
        	urlStr += encodeURIComponent("monthlyCharge") + '=' + encodeURIComponent($("#totalCharge").val()) + "&";
        }
        if($("#selectPack").val() !== "") {
        	urlStr += encodeURIComponent("packId") + '=' + encodeURIComponent($("#selectPack").val()) + "&";
        }
        if($("#selectRent").val() !== "") {
        	urlStr += encodeURIComponent("packPrice") + '=' + encodeURIComponent($("#selectRent").val()) + "&";
        }
        return urlStr;
	}
	
});

