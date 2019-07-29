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
				closeAfterAdd: true,
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
		afterShowForm: function (formid) {
			manageFields();
			disableFields();
        },
		mtype: "POST"
	};
	var addOptions = {
			width: 700,
		onclickSubmit: function(params, postdata) {
			params.url = 'customerPayment';
		},
		afterShowForm: function (formid) {
			selectToFields();
			manageFields();
        },
        afterSubmit: function (response, postdata) {
			setTimeout(function() { 
				$('#add_customerPayments').click();
			}, 200);
			return [true, '', response.responseText];
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'customerPayment/' + postdata;
		}
	};

	var URL = '/allCustomerPayments';
	var allCustomers;
	var options = {
		url: URL,
		editurl: URL,
		height: 'auto',
        forceFit: true,
        autowidth: true,
        shrinkToFit: true,
        rowNum: 50,
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
					if(rowObject) {
						return rowObject.customer.name;
					}
					return "";
				},
		        editoptions:{
                    dataUrl: "/getAllCustomers", 
                           buildSelect: function(jsonOrderArray) {
                                   var s = '<select>';
                                   s += '<option value="">Select Customer</option>';
                                   if (jsonOrderArray && jsonOrderArray.length) {
                                	   var myObj = JSON.parse(jsonOrderArray);
                                	   allCustomers = myObj;
                                	   for (var key in myObj) {
                                		    s += '<option value="'+key+'">'+myObj[key].name+'</option>';
                                		}
                                  }
                                  return s + "</select>";
                          }
                   },
				editrules: {required: true}
			},
			{
				name:"customerCode",
				label: 'Customer Code',
				index:"customerCode",
				formatter: function myformatter ( cellvalue, options, rowObject ) {
					if(rowObject) {
						return rowObject.customer.customerCode;
					}
					return "";
				},
				editable: true,
			},
			{
				name:"amountCredit",
				label: 'Amount',
				index:"amountCredit",
				formatter:'number',
				editable: true,
			},
			{
				name:"paymentDate",
				label: 'Payment Date',
				index:"paymentDate",
				formatter:'date',
				formatoptions: { newformat: 'Y/m/d'},
				editable: true,
				editoptions: {
			      dataInit: function(element) {
			        $(element).datepicker({dateFormat: 'yy/mm/dd'})
			      }
			    }
			},
			{
				name:'paymentType',
				label: 'Payment Type',
				index: 'paymentType',
				editable: true,
				edittype:"select",
		        editoptions:{ value: 'RENTAL:RENTAL;BOX:BOX' },
				editrules: {required: true}
			},
			{
				name:'paymentMode',
				label: 'Payment Mode',
				index: 'paymentMode',
				editable: true,
				edittype:"select",
		        editoptions:{ value: 'CASH:CASH;CHEQUE:CHEQUE' },
				editrules: {required: true}
			},
			{
				name:"chequeDate",
				label: 'Cheque Date',
				index:"chequeDate",
				formatter:'date',
				formatoptions: { newformat: 'Y/m/d'},
				editable: true,
				editoptions: {
			      dataInit: function(element) {
			        $(element).datepicker({dateFormat: 'yy/mm/dd'})
			      }
			    }
			},
			{
				name:"chequeNumber",
				label: 'Cheque Number',
				index:"chequeNumber",
				editable: true
			}
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

	function selectToFields() {
		setTimeout(function() { 
			$("#customer\\.id").addClass("ui-widget ui-jqdialog");
			$("#customer\\.id").select2();
			$("#customer\\.id").select2('open');
		}, 200);
	}
	
	function manageFields() {
		setTimeout(function() { 
			$("#customerCode").attr("disabled","disabled");
			$("#customerCode").attr("readonly","readonly");
			if($("#paymentMode").val() == "CASH") {
				$("#chequeNumber").attr("disabled","disabled");
				$("#chequeNumber").attr("readonly","readonly");
				$("#chequeDate").attr("disabled","disabled");
				$("#chequeDate").attr("readonly","readonly");
			}
			
			$("#customer\\.id").bind("change", function (e) {
				var selectedId = $(this).val();
				$("#customerCode").val(allCustomers[selectedId].customerCode);
			});
			
			$("#paymentMode").bind("change", function (e) {
				if($(this).val() == "CASH") {
					$("#chequeNumber").attr("disabled","disabled");
					$("#chequeNumber").attr("readonly","readonly");
					$("#chequeDate").attr("disabled","disabled");
					$("#chequeDate").attr("readonly","readonly");
				} else {
					$("#chequeNumber").removeAttr("disabled","disabled");
					$("#chequeNumber").removeAttr("readonly","readonly");
					$("#chequeDate").removeAttr("disabled","disabled");
					$("#chequeDate").removeAttr("readonly","readonly");
				}
			});
		}, 200);
	}
	
	function disableFields(){
		
	}
});
