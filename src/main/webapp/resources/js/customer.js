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
				sortname: 'name',
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
				closeAfterAdd: true,
			    recreateForm: true,
				ajaxEditOptions: { contentType: "application/x-www-form-urlencoded" },
				mtype: 'POST',
				serializeEditData: function(data) {
					var url = Object.keys(data).map(function(k) {
					    return encodeURIComponent(k) + '=' + encodeURIComponent(data[k])
					}).join('&');
					return url;
					//encodeURIComponent("id") + '=' + encodeURIComponent(data.networkChannel_id);
					//delete data.oper;
					//return JSON.stringify(data);
				}
			});
	$.extend($.jgrid.del, {
				mtype: 'DELETE',
				serializeDelData: function() {
					return "";
				}
			});

	var editOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'customer/' + postdata.id;
		}
	};
	var addOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'customer';
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'customer/' + postdata;
		}
	};

	var networksData;
	var URL = '/allCustomers';
	var packs;
	var options = {
		url: URL,
		editurl: URL,
		height: 'auto',
		forceFit: true,
        autowidth: true,
        rowNum: 10,
		colModel:[
			{
				name:'id',
				label: 'ID',
				index: 'id',
				formatter:'integer',
				editable: true,
				editoptions: {disabled: true, size:5}
			},
			{
				name:'name',
				label: 'Name',
				index: 'name',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'customerCode',
				label: 'Customer Code',
				index: 'customerCode',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'customerType',
				label: 'Customer Type',
				index: 'customerType',
				editable: true,
				editrules: {required: true},
				edittype:'select', 
				formatter:'select', 
				editoptions:{value:"NORMAL:NORMAL;GOOD:GOOD"}
			},
			{
				name:'address',
				label: 'Address',
				index: 'address',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'city',
				label: 'City',
				index: 'city',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'mobile',
				label: 'Mobile',
				index: 'mobile',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'landLine',
				label: 'Land Line',
				index: 'landLine',
				editable: true,
				editrules: {required: true}
			},			
			{
				name:'area.id',
				label: 'Area',
				index: 'area.id',
				editable: true,
				edittype:"select",
				formatter: function myformatter ( cellvalue, options, rowObject ) {
					console.info(rowObject);
					if(rowObject.area) {
						return rowObject.area.name;
					}
					return "";
				},
		        editoptions:{
                    dataUrl: "/getAllAreas", 
                           buildSelect: function(jsonOrderArray) {
                                   var s = '<select>';
                                   if (jsonOrderArray && jsonOrderArray.length) {
                                	   var myObj = JSON.parse(jsonOrderArray);
                                	   for (var key in myObj) {
                                		    console.log(key + ': ' + myObj[key]);
                                		    s += '<option value="'+key+'">'+myObj[key]+'</option>';
                                		}
                                  }
                                  return s + "</select>";
                          }
                   },
				editrules: {required: true}
			
			},
			{
				name:'subArea.id',
				label: 'Sub Area',
				index: 'subArea.id',
				editable: true,
				edittype:"select",
				formatter: function myformatter ( cellvalue, options, rowObject ) {
					console.info(rowObject);
					if(rowObject.area) {
						return rowObject.area.name;
					}
					return "";
				},
		        editoptions:{
                    dataUrl: "/getAllSubAreas", 
                           buildSelect: function(jsonOrderArray) {
                                   var s = '<select>';
                                   if (jsonOrderArray && jsonOrderArray.length) {
                                	   var myObj = JSON.parse(jsonOrderArray);
                                	   for (var key in myObj) {
                                		    s += '<option value="'+key+'">'+myObj[key]+'</option>';
                                		}
                                  }
                                  return s + "</select>";
                          }
                   },
				editrules: {required: true}
			
			},
			{

				name:'street.id',
				label: 'Street',
				index: 'street.id',
				editable: true,
				edittype:"select",
				formatter: function myformatter ( cellvalue, options, rowObject ) {
					console.info(rowObject);
					if(rowObject.area) {
						return rowObject.area.name;
					}
					return "";
				},
		        editoptions:{
                    dataUrl: "/getAllStreets", 
                           buildSelect: function(jsonOrderArray) {
                                   var s = '<select>';
                                   if (jsonOrderArray && jsonOrderArray.length) {
                                	   var myObj = JSON.parse(jsonOrderArray);
                                	   for (var key in myObj) {
                                		    s += '<option value="'+key+'">'+myObj[key]+'</option>';
                                		}
                                  }
                                  return s + "</select>";
                          }
                   },
				editrules: {required: true}
			
			
			}
		],
		caption: "Customers",
		pager : '#pagerCustomers',
		height: 'auto',
		forceFit: true,
        autowidth: true,
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		},
		multiselect: false,
		subGrid: true,
		caption: "Customers",
		subGridRowExpanded: function(subgrid_id, row_id) {
			var subgrid_table_id, pager_id;
			subgrid_table_id = subgrid_id+"_t";
			pager_id = "p_"+subgrid_table_id;
			$("#"+subgrid_id).html("<table id='"+subgrid_table_id+"' class='scroll'></table><div id='"+pager_id+"' class='scroll'></div>");
			jQuery("#"+subgrid_table_id).jqGrid({
				url:"allCustomerSetTopBoxes/"+row_id,
				editurl: 'updateCustomerSetTopBox/' + row_id,
				colNames: ['Id','Set Top Box', "Pack", "Pack Price", "Payment Mode", "Entry Date", "Payment Start Date", "Billing Cycle", "Opening Balance", "Discount", "Discount Frequency"],
				colModel: [
					{
						name:"id",
						index:"id",
						formatter:'integer',
						editable: true,
						editoptions: 
						{disabled: true}
					},
					{
						name:"setTopBox.id",
						label: 'Set Top Box',
						index:"setTopBox.id", 
						editable: true,
						editrules: {required: true},
						edittype:"select",
						formatter: function myformatter ( cellvalue, options, rowObject ) {
							console.info(rowObject);
							return rowObject.setTopBox.setTopBoxNumber;
						},
				        editoptions:{
		                    dataUrl: "/getAllSetTopBoxes", 
		                           buildSelect: function(jsonOrderArray) {
		                                   var s = '<select>';
		                                   console.info(jsonOrderArray);
		                                   if (jsonOrderArray && jsonOrderArray.length) {
		                                	   var myObj = JSON.parse(jsonOrderArray);
		                                	   console.info("-------------------------");
		                                	   console.info(myObj);
		                                	   for (var key in myObj) {
		                                		    s += '<option value="'+key+'">'+myObj[key]+'</option>';
		                                		}
		                                  }
		                                  return s + "</select>";
		                          }
		                   }
					},
					{
						name:'pack.id',
						label: 'Pack',
						index: 'pack.id',
						editable: true,
						edittype:"select",
						formatter: function myformatter ( cellvalue, options, rowObject ) {
							if(rowObject.pack) {
								return rowObject.pack.name;
							}
							return "";
						},
				        editoptions:{
		                    dataUrl: "/getAllPacks", 
		                           buildSelect: function(jsonOrderArray) {
		                                   var s = '<select>';
		                                   console.info("000000000000");
		                                   console.info(jsonOrderArray);
		                                   if (jsonOrderArray && jsonOrderArray.length) {
		                                	   var myObj = JSON.parse(jsonOrderArray);
		                                	   packs = myObj;
		                                	   console.info(myObj);
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
						name:"packPrice",
						label: 'Pack Price',
						index:"packPrice",
						formatter:'number',
						editable: true,
					},
					{
						name:"paymentMode",
						label: 'Payment Mode',
						index:"paymentMode",
						editable: true,
						formatter:'select',
						edittype:"select",
						editrules: {required: true},
						editoptions: {value: "PREPAID:PREPAID;POSTPAID:POSTPAID"}
					},
					{
						name:"entryDate",
						label: 'Entry Date',
						index:"entryDate",
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
						name:"paymentStartDate",
						label: 'Payment Start Date',
						index:"paymentStartDate",
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
						name:"billingCycle",
						label: 'Billing Cycle',
						index:"billingCycle",
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
						name:"openingBalance",
						label: 'Opening Balance',
						index:"openingBalance",
						formatter:'number',
						editable: true	
					},
					{
						name:"discount",
						label: 'Discount',
						index:"discount",
						formatter:'number',
						editable: true	
					},
					{
						name:"discountFrequency",
						label: 'Discount Frequency',
						index:"discountFrequency",
						editable: true,
						formatter:'select',
						editrules: {required: true},
						edittype:"select",
						editoptions: {value: "MONTHLY:MONTHLY;ONE_TIME:ONE TIME"}
					}
				],
			   	rowNum:10,
			   	pager: pager_id,
			   	sortname: 'name',
			    sortorder: "asc",
			    height: '100%',
			    forceFit: true,
		        autowidth: true,
			    subGrid: true,
			    forceFit: true,
                caption: "Set Top Boxes",
                subGridRowExpanded: function(subgrid_id3, row_id3) {
                    var subgrid_table_id3, pager_id3;
                    subgrid_table_id3 = subgrid_id3+"_t";
                    pager_id3 = "p_"+subgrid_table_id3;
        			$("#"+subgrid_id3).html("<table id='"+subgrid_table_id3+"' class='scroll'></table><div id='"+pager_id3+"' class='scroll'></div>");
                    jQuery("#"+subgrid_table_id3).jqGrid({
                        url:"allCustomerSetTopBoxChannels/"+row_id3,
                        colNames: ['Id', "Channel"],
                        colModel: [
                        	{
        						name:"id",
        						index:"id",
        						formatter:'integer',
        						editable: true,
        						editoptions: 
        						{disabled: true}
        					},
        					{
        						name:"networkChannelId",
        						label: 'Channel',
        						index:"networkChannelId", 
        						editable: true,
        						editrules: {required: true},
        						edittype:"select",
        						formatter: function myformatter ( cellvalue, options, rowObject ) {
        							console.info(rowObject);
        							return rowObject.name;
        						},
        				        editoptions:{
        		                    dataUrl: "/getAllNetworkChannels", 
        		                           buildSelect: function(jsonOrderArray) {
        		                                   var s = '<select>';
        		                                   if (jsonOrderArray && jsonOrderArray.length) {
        		                                	   var myObj = JSON.parse(jsonOrderArray);
        		                                	   for (var key in myObj) {
        		                                		    s += '<option value="'+key+'">'+myObj[key].name+'</option>';
        		                                		}
        		                                  }
        		                                  return s + "</select>";
        		                          }
        		                   }
        					}
                            ],
                            caption: "Channels",
                        	rowNum:10,
            			   	pager: pager_id3,
            			   	sortname: 'name',
            			    sortorder: "asc",
            			    height: '100%',
            			    forceFit: true,
            		        autowidth: true
                    });
                    
                	var addOptionsSG3 = {
        				onclickSubmit: function(params, postdata) {
        					params.url = 'addCustomerNetworkChannel/' + row_id + '/' + row_id3;
        				},
        				mtype: "POST"
        			};
        			var delOptionsSG3 = {
        				onclickSubmit: function(params, postdata) {
        					params.url = 'pack/' + postdata;
        				}
        			};
        			
        			jQuery("#"+subgrid_table_id3).jqGrid('navGrid',"#"+pager_id3,
        					{edit:false,add:true,del:true, addtext: 'Add', edittext: 'Edit',deltext: 'Delete'}, 
        					{},
        					addOptionsSG3,
        					delOptionsSG3
        				);
                }
			});
		
			var editOptionsSG = {
					onclickSubmit: function(params, postdata) {
						params.url = 'updateCustomerSetTopBox/' + row_id;
					},
					afterShowForm: function (formid) {
						manageFieldsForEdit();
	                }
				};
			var addOptionsSG = {
				onclickSubmit: function(params, postdata) {
					params.url = 'createCustomerSetTopBox/' + row_id;
				},
				afterShowForm: function (formid) {
					manageFieldsForAdd();
                },
				mtype: "POST"
			};
			var delOptionsSG = {
				onclickSubmit: function(params, postdata) {
					params.url = 'pack/' + postdata;
				}
			};
			
			jQuery("#"+subgrid_table_id).jqGrid('navGrid',"#"+pager_id,
					{edit:true,add:true,del:false, addtext: 'Add', edittext: 'Edit'}, 
					editOptionsSG,
					addOptionsSG,
					delOptionsSG
				);
			$("#"+subgrid_table_id).navButtonAdd("#"+pager_id,
	                {
	                    buttonicon: "ui-icon-trash",
	                    title: "Delete",
	                    caption: "Delete",
	                    position: "last",
	                    onClickButton: function() {
	                    	var myGrid = $("#"+subgrid_table_id);
	                    	selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
	                    	if(selectedRowId) {
	                    		cellValue = myGrid.jqGrid ('getCell', selectedRowId, 'id');
	                    		$("#customerId").val(row_id);
	                    		$("#customerSetTopBoxId").val(cellValue);
		                    	$("#myDialog").dialog('open');
	                    	} else {
	                    		$("#mySelectRowDialog").dialog('open');
	                    	}
	                    }
	                });
		},
		subGridRowColapsed: function(subgrid_id, row_id) {
			// this function is called before removing the data
			var subgrid_table_id;
			subgrid_table_id = subgrid_id+"_t";
			jQuery("#"+subgrid_table_id).remove();
		}
	};
	
	$("#customers")
			.jqGrid(options)
			.navGrid('#pagerCustomers',
			{addtext: 'Add', edittext: 'Edit',deltext: 'Delete'}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#customers").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });
	
	function manageFieldsForAdd() {
		setTimeout(function() { 
			billingCycleState();
			$("#editmodcustomers").attr("width", "50%");
			var packSelected = $("#pack\\.id").val();
			if(packs) {
				$("#packPrice").val(packs[packSelected].price);
			}
			$("#pack\\.id").bind("change", function (e) {
				$("#packPrice").val(packs[$(this).val()].price);
			});
			$("#paymentMode").bind("change", function (e) {
				billingCycleState();
			});
		}, 100);
    }
	
	function manageFieldsForEdit() {
		setTimeout(function() { 
			$("#editmodcustomers").attr("width", "50%");
			$("#paymentMode").attr("disabled","disabled");
			$("#paymentMode").attr("readonly","readonly");
			billingCycleState();
			var packSelected = $("#pack\\.id").val();
			if(packs) {
				$("#packPrice").val(packs[packSelected].price);
			}
			$("#pack\\.id").bind("change", function (e) {
				$("#packPrice").val(packs[$(this).val()].price);
			});
			$("#paymentMode").bind("change", function (e) {
				billingCycleState();
			});
		}, 100);
    }

	$("#myDialog").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "A Dialog Box",
		buttons : {
			'OK' : function() {
				$.post("removeCustomerSetTopBox/" + $('#customerId').val(), {
					id : $('#customerSetTopBoxId').val(),
					setTopBoxStatus: $("#status").val(),
					reason: $("#reason").val(),
					amount : $("#amount").val()
				}).done(function(data) {
					$(this).dialog('close');
				});
				// Now you have the value of the textbox, you can do something
				// with it, maybe an AJAX call to your server!
			},
			'Close' : function() {
				$(this).dialog('close');
			}
		}
	});
	
	$("#mySelectRowDialog").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "Select Row",
		buttons : {
			'Ok' : function() {
				$(this).dialog('close');
			}
		}
	});
	
	function billingCycleState() {
		if($("#paymentMode").val() === "PREPAID") {
			$("#billingCycle").attr("disabled","disabled");
			$("#billingCycle").attr("readonly","readonly");
			$("#billingCycle").val('');
		} else {
			$("#billingCycle").datepicker({ defaultDate: new Date() });
			$("#billingCycle").removeAttr("disabled","disabled");
			$("#billingCycle").removeAttr("readonly","readonly");
		}
	}
});
