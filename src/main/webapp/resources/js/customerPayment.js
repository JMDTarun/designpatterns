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
				sortname: 'id',
				sortorder: 'asc',
				height: 'auto',
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
						console.info(k+"!!!"+data[k]);
					    return encodeURIComponent(k) + '=' + encodeURIComponent(data[k].replace("_empty", ""))
					}).join('&');
					return url;
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
			params.url = 'customerPayment/' + postdata.id;
		},
		mtype: "POST"
	};
	var addOptions = {
			width: 700,
		onclickSubmit: function(params, postdata) {
			params.url = 'customerPayment';
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'customerPayment/' + postdata;
		}
	};

	var URL = '/allCustomerPayments';
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
				name:'id',
				label: 'ID',
				index: 'id',
				formatter:'integer',
				editable: true,
				hidden: true, 
				editrules: { edithidden: false }
			},
			{
				name:'customer.id',
				label: 'Customer',
				index: 'customer.id',
				editable: true,
				edittype:"select",
				formatter: function myformatter ( cellvalue, options, rowObject ) {
					if(rowObject.customerType) {
						return rowObject.customerType.customerType;
					}
					return "";
				},
		        editoptions:{
                    dataUrl: "/getAllCustomers", 
                           buildSelect: function(jsonOrderArray) {
                                   var s = '<select>';
                                   if (jsonOrderArray && jsonOrderArray.length) {
                                	   var myObj = JSON.parse(jsonOrderArray);
                                	   for (var key in myObj) {
                                		    s += '<option value="'+key+'">'+myObj[key].customerCode + ' - '+ myObj[key].name+'</option>';
                                		}
                                  }
                                  return s + "</select>";
                          }
                   },
				editrules: {required: true}
			},
			/*{
				name:'areaCode',
				label: 'Area Code',
				index: 'areaCode',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'lcoCode',
				label: 'Lco Code',
				index: 'lcoCode',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'lcoName',
				label: 'Lco Name',
				index: 'lcoName',
				editable: true,
				editrules: {required: true}
			}*/
		],
		caption: "Customer Payments",
		pager : '#pagerCustomerPayments',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#customerPayments")
			.jqGrid(options)
			.navGrid('#pagerCustomerPayments',
			{addtext: 'Add', search:false, edittext: 'Edit',deltext: 'Delete'}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#pagerCustomerPayments").css({"height":"55"});
	
	$("#customerPayments").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });

});
