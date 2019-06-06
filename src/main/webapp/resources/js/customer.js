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
			width: 700,
		onclickSubmit: function(params, postdata) {
			params.url = 'customer/' + postdata.id;
		}
	};
	var addOptions = {
			width: 700,
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
	var setTopBoxGridId;
	var channelGridId;
	var setTopBoxSelectedRowId;
	
	var packs;
	var options = {
		url: URL,
		editurl: URL,
		height: $("#customerContainer").height(),
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
				name:'customerType.id',
				label: 'Customer Type',
				index: 'customerType.id',
				editable: true,
				edittype:"select",
				formatter: function myformatter ( cellvalue, options, rowObject ) {
					console.info(rowObject);
					if(rowObject.customerType) {
						return rowObject.customerType.customerType;
					}
					return "";
				},
		        editoptions:{
                    dataUrl: "/getAllCustomerTypes", 
                           buildSelect: function(jsonOrderArray) {
                                   var s = '<select>';
                                   if (jsonOrderArray && jsonOrderArray.length) {
                                	   var myObj = JSON.parse(jsonOrderArray);
                                	   for (var key in myObj) {
                                		    console.log(key + ': ' + myObj[key]);
                                		    s += '<option value="'+key+'">'+myObj[key].customerType+'</option>';
                                		}
                                  }
                                  return s + "</select>";
                          }
                   },
				editrules: {required: true}
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
		height: $("#customerContainer").height(),
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
				colNames: ['Id', 'Set Top box Id', 'Activate Date', 'Deactivate Date', 'Set Top Box', "Pack", "Pack Price", "Payment Mode", "Entry Date", "Payment Start Date", "Billing Cycle", "Opening Balance", "Discount", "Discount Frequency", "Active"],
				colModel: [
					{
						name:"id",
						index:"id",
						formatter:'integer',
						editable: true,
						editoptions: 
						{disabled: true},
						hidden:true
					},
					{
						name:"setTopBoxId",
						index:"setTopBoxId",
						formatter:'integer',
						editable: false,
						editoptions: {disabled: true},
						editrules:{edithidden:true},
						hidden: true,
						formatter: function myformatter ( cellvalue, options, rowObject ) {
							return rowObject.setTopBox.id;
						}
					},
					{
						name:"activateDate",
						label: 'Activate Date',
						index:"activateDate",
						formatter:'date',
						formatoptions: { newformat: 'Y/m/d'},
						editable: false,
						editoptions: {disabled: true},
						editrules:{edithidden:true},
					    hidden:true
					},
					{
						name:"deactivateDate",
						label: 'Deactivate Date',
						index:"deactivateDate",
						formatter:'date',
						formatoptions: { newformat: 'Y/m/d'},
						editable: false,
						editoptions: {disabled: true},
						editrules:{edithidden:true},
					    hidden:true
					},
					{
						name:"setTopBox.id",
						label: 'Set Top Box',
						index:"setTopBox.id", 
						editable: true,
						editrules: {required: true},
						edittype:"select",
						formatter: function myformatter ( cellvalue, options, rowObject ) {
							return rowObject.setTopBox.setTopBoxNumber;
						},
				        editoptions:{
		                    dataUrl: "/getAllSetTopBoxes", 
	                           buildSelect: function(jsonOrderArray) {
                                   var s = '<select>';
                                   if (jsonOrderArray && jsonOrderArray.length) {
                                	   s += '<option value="">Please Select Set Top Box</option>';
                                	   var myObj = JSON.parse(jsonOrderArray);
                            		   for (var key in myObj) {
                               		    s += '<option value="'+key+'">'+myObj[key]+'</option>';
                            		   }
                                  }
                                  return s + "</select>";
	                          },
	                          selectFilled: function (options) {
	                        	    $(options.elem).select2({
	                        	        dropdownCssClass: "ui-widget ui-jqdialog",
	                        	        width: "100%"
	                        	    });
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
	                                   if (jsonOrderArray && jsonOrderArray.length) {
	                                	   var myObj = JSON.parse(jsonOrderArray);
	                                	   packs = myObj;
	                                	   console.info(myObj);
	                                	   for (var key in myObj) {
	                                		    s += '<option value="'+key+'">'+myObj[key].name+'</option>';
	                                	   }
	                                  }
	                                  return s + "</select>";
	                          },
	                          selectFilled: function (options) {
	                        	    $(options.elem).select2({
	                        	        dropdownCssClass: "ui-widget ui-jqdialog",
	                        	        width: "100%"
	                        	    });
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
					},
					{
						name:"active",
						index:"active",
						formatter:'integer',
						editable: false,
						editoptions: 
						{disabled: true}
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
                beforeSelectRow: function (rowid, e) {
		        	setTopBoxGridId = subgrid_table_id;
		        	setTopBoxSelectedRowId = rowid;
		            return true;
		        },
                subGridRowExpanded: function(subgrid_id3, row_id3) {
                    var subgrid_table_id3, pager_id3;
                    subgrid_table_id3 = subgrid_id3+"_t";
                    pager_id3 = "p_"+subgrid_table_id3;
        			$("#"+subgrid_id3).html("<table id='"+subgrid_table_id3+"' class='scroll'></table><div id='"+pager_id3+"' class='scroll'></div>");
                    jQuery("#"+subgrid_table_id3).jqGrid({
                        url:"allCustomerSetTopBoxChannels/"+row_id3,
                        colNames: ['Id', "Channel", "Entry Date", "Payment Start Date", "Deleted"],
                        colModel: [
                        	{
        						name:"id",
        						index:"id",
        						formatter:'integer',
        						editable: false,
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
        							if(rowObject.networkChannel) {
        								return rowObject.networkChannel.name;
        							}
        							return '';
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
        						name:"deleted",
        						index:"deleted",
        						formatter:'integer',
        						editable: false,
        						editoptions: 
        						{disabled: true}
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
        				mtype: "POST",
        				width: 700
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
        			
        			$("#"+subgrid_table_id3).navButtonAdd("#"+pager_id3,
        	                {
        	                    buttonicon: "ui-icon-trash",
        	                    title: "Remove Channel",
        	                    caption: "Remove Channel",
        	                    position: "last",
        	                    onClickButton: function() {
        	                    	var myGrid = $("#"+subgrid_table_id3);
        	                    	channelGridId = myGrid;
        	                    	selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
        	                    	if(selectedRowId) {
        	                    		cellValue = myGrid.jqGrid ('getCell', selectedRowId, 'id');
        	                    		$("#rcCustomerId").val(row_id);
        	                    		$("#rcCustomerSetTopBoxId").val(row_id3);
        	                    		$("#rcCid").val(cellValue);
        	                    		$("#channelRemoveDate").datepicker({ defaultDate: new Date() });
        		                    	$("#channelRemoveDialog").dialog('open');
        	                    	} else {
        	                    		$("#mySelectRowDialog").dialog('open');
        	                    	}
        	                    }
        	                });
                }
			});
		
			var editOptionsSG = {
					width: 700,
					onclickSubmit: function(params, postdata) {
						params.url = 'updateCustomerSetTopBox/' + row_id;
					},
					afterShowForm: function (formid) {
						manageFieldsForEdit();
	                },
	                width: 500
				};
			var addOptionsSG = {
					width: 700,
				onclickSubmit: function(params, postdata) {
					params.url = 'createCustomerSetTopBox/' + row_id;
				},
				afterShowForm: function (formid) {
					manageFieldsForAdd();
                },
				mtype: "POST",
				width: 500
			};
			var delOptionsSG = {
				onclickSubmit: function(params, postdata) {
					params.url = 'pack/' + postdata;
				}
			};
			
			$("#"+subgrid_table_id).jqGrid('navGrid',"#"+pager_id,
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
			
			$("#"+subgrid_table_id).navButtonAdd("#"+pager_id,
	                {
	                    buttonicon: "ui-icon-trash",
	                    title: "Activate",
	                    caption: "Activate",
	                    position: "last",
	                    onClickButton: function() {
	                    	var myGrid = $("#"+subgrid_table_id);
	                    	selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
	                    	setTopBoxGridId = myGrid;
	                    	if(selectedRowId) {
	                    		idCellValue = myGrid.jqGrid ('getCell', selectedRowId, 'id');
	                    		isActiveCell = myGrid.jqGrid ('getCell', selectedRowId, 'active');
	                    		if(isActiveCell == 1) {
	                    			$("#setTopBoxAlreadyActive").dialog('open');
	                    		} else {
	                    			$("#activateCustomerId").val(row_id);
		                    		$("#activateCustomerSetTopBoxId").val(idCellValue);
		                    		$("#activateDate").datepicker({ defaultDate: new Date(), minDate: new Date(myGrid.jqGrid ('getCell', selectedRowId, 'deactivateDate'))  });
			                    	$("#setTopBoxActivate").dialog('open');
	                    		}
	                    		
	                    	} else {
	                    		$("#mySelectRowDialog").dialog('open');
	                    	}
	                    }
	                });
			
			$("#"+subgrid_table_id).navButtonAdd("#"+pager_id,
	                {
	                    buttonicon: "ui-icon-trash",
	                    title: "Deactivate",
	                    caption: "Deactivate",
	                    position: "last",
	                    onClickButton: function() {
	                    	var myGrid = $("#"+subgrid_table_id);
	                    	selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
	                    	setTopBoxGridId = myGrid;
	                    	if(selectedRowId) {
	                    		cellValue = myGrid.jqGrid ('getCell', selectedRowId, 'id');
	                    		isActiveCell = myGrid.jqGrid ('getCell', selectedRowId, 'active');
	                    		if(isActiveCell == 0) {
	                    			$("#setTopBoxAlreadyDeactive").dialog('open');
	                    		} else {
	                    			$("#deactivateCustomerId").val(row_id);
	                    			$("#deactivateCustomerSetTopBoxId").val(cellValue);
	                    			$("#deactivateDate").datepicker({ defaultDate: new Date() });
	                    			$("#setTopBoxDeactivate").dialog('open');
	                    		}
	                    	} else {
	                    		$("#mySelectRowDialog").dialog('open');
	                    	}
	                    }
	                });
			
			$("#"+subgrid_table_id).navButtonAdd("#"+pager_id,
	                {
	                    buttonicon: "ui-icon-trash",
	                    title: "Replace",
	                    caption: "Replace",
	                    position: "last",
	                    onClickButton: function() {
	                    	var myGrid = $("#"+subgrid_table_id);
	                    	selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
	                    	if(selectedRowId) {
	                    		cellValue = myGrid.jqGrid ('getCell', selectedRowId, 'id');
	                    		$("#replaceCustomerId").val(row_id);
	                    		getAllSetTopBoxes();
	                    		
	                			$('#currentSetTopBox').find('option').remove().end().append('<option value="'+myGrid.jqGrid ('getCell', selectedRowId, 'setTopBoxId')+'">'+myGrid.jqGrid ('getCell', selectedRowId, 'setTopBox.id')+'</option>');
	                			$("#currentSetTopBox").addClass("ui-widget ui-jqdialog");
	                			$("#currentSetTopBox").select2();
	                			$("#currentSetTopBox").attr("disabled","disabled");
	                			$("#currentSetTopBox").attr("readonly","readonly");

	                			$("#setTopBoxReplace").dialog('open');
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
	
	$("#customers").navButtonAdd("#pagerCustomers",
            {
                buttonicon: "ui-icon-circle-plus",
                title: "Add Discount",
                caption: "Add Discount",
                position: "last",
                onClickButton: function() {
                	var myGrid = $("#customers");
                	selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
                	if(selectedRowId) {
                		cellValue = myGrid.jqGrid ('getCell', selectedRowId, 'id');
                		$("#adCustomerId").val(selectedRowId);
                		$("#adCustomerSetTopBoxId").val(cellValue);
                    	$("#myAdditionalDiscountDialog").dialog('open');
                	} else {
                		$("#mySelectRowDialog").dialog('open');
                	}
                }
            });
	
	$("#customers").navButtonAdd("#pagerCustomers",
            {
                buttonicon: "ui-icon-circle-plus",
                title: "Add Charge",
                caption: "Add Charge",
                position: "last",
                onClickButton: function() {
                	var myGrid = $("#customers");
                	selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
                	if(selectedRowId) {
                		cellValue = myGrid.jqGrid ('getCell', selectedRowId, 'id');
                		$("#acCustomerId").val(selectedRowId);
                		$("#acCustomerSetTopBoxId").val(cellValue);
                    	$("#myAdditionalChargeDialog").dialog('open');
                	} else {
                		$("#mySelectRowDialog").dialog('open');
                	}
                }
            });
	
	function manageFieldsForAdd() {
		setTimeout(function() { 
			billingCycleState();
			$("#editmodcustomers").attr("width", "50%");
			addSelect2();
			
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
			var myGrid = $("#"+setTopBoxGridId);
			selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
			$("#editmodcustomers").attr("width", "50%");
			$("#paymentMode").attr("disabled","disabled");
			$("#paymentMode").attr("readonly","readonly");
			$("#paymentMode").val(myGrid.jqGrid ('getCell', selectedRowId, 'paymentMode'));
			$('#setTopBox\\.id').find('option').remove().end().append('<option value="'+myGrid.jqGrid ('getCell', selectedRowId, 'setTopBoxId')+'">'+myGrid.jqGrid ('getCell', selectedRowId, 'setTopBox.id')+'</option>');
			//$("#setTopBox\\.id").val(myGrid.jqGrid ('getCell', selectedRowId, 'setTopBox.id'));
			$("#setTopBox\\.id").attr("disabled","disabled");
			$("#setTopBox\\.id").attr("readonly","readonly");
			console.info("!!!!! "+myGrid.jqGrid ('getCell', selectedRowId, 'setTopBoxId'));

			addSelect2();
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

	function addSelect2() {
		$("#pack\\.id").addClass("ui-widget ui-jqdialog");
		$("#pack\\.id").select2();
		
		$("#setTopBox\\.id").addClass("ui-widget ui-jqdialog");
		$("#setTopBox\\.id").select2();
	}
	
	$("#myDialog").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "Remove Set Top Box",
		buttons : {
			'OK' : function() {
				$.post("removeCustomerSetTopBox/" + $('#customerId').val(), {
					id : $('#customerSetTopBoxId').val(),
					setTopBoxStatus: $("#status").val(),
					reason: $("#reason").val(),
					amount : $("#amount").val()
				}).done(function(data) {
					$(this).dialog('close');
					$(setTopBoxGridId).trigger( 'reloadGrid' );
				});
				// Now you have the value of the textbox, you can do something
				// with it, maybe an AJAX call to your server!
			},
			'Close' : function() {
				$(this).dialog('close');
			}
		}
	});

	$("#myAdditionalDiscountDialog").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "Remove Set Top Box",
		buttons : {
			'OK' : function() {
				$.post("addAdditionalDiscount/" + $('#adCustomerId').val(), {
					id : $('#adCustomerSetTopBoxId').val(),
					reason: $("#reason").val(),
					amount : $("#additionalDiscount").val()
				}).done(function(data) {
					$("#myAdditionalDiscountDialog").dialog('close');
				});
			},
			'Close' : function() {
				$(this).dialog('close');
			}
		}
	});
	
	$("#myAdditionalChargeDialog").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "Remove Set Top Box",
		buttons : {
			'OK' : function() {
				$.post("addAdditionalCharge/" + $('#acCustomerId').val(), {
					id : $('#acCustomerSetTopBoxId').val(),
					reason: $("#adReason").val(),
					amount : $("#additionalCharge").val()
				}).done(function(data) {
					$("#myAdditionalChargeDialog").dialog('close');
				});
			},
			'Close' : function() {
				$("#myAdditionalChargeDialog").dialog('close');
			}
		}
	});
	
	$("#mySelectRowDialog").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "Select Set Top Box",
		buttons : {
			'Ok' : function() {
				$(this).dialog('close');
			}
		}
	});
	
	$("#setTopBoxAlreadyActive").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "Set Top Box Already Active",
		buttons : {
			'Ok' : function() {
				$(this).dialog('close');
			}
		}
	});
	
	$("#setTopBoxAlreadyDeactive").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "Set Top Box Already Deactive",
		buttons : {
			'Ok' : function() {
				$(this).dialog('close');
			}
		}
	});
	
	$("#channelRemoveDialog").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "Remove Channel",
		buttons : {
			'OK' : function() {
				$.post("removeCustomeNetworkChannel/" + $('#rcCustomerId').val(), {
					customerSetTopBoxId : $('#rcCustomerSetTopBoxId').val(),
					customerId: $("#rcCustomerId").val(),
					id: $("#rcCid").val(),
					channelRemoveDate: $("#channelRemoveDate").val(),
					reason : $("#rcReason").val()
				}).done(function(data) {
					$("#channelRemoveDialog").dialog('close');
					$(channelGridId).trigger( 'reloadGrid' );
				});
				// Now you have the value of the textbox, you can do something
				// with it, maybe an AJAX call to your server!
			},
			'Close' : function() {
				$(this).dialog('close');
			}
		}
	});
	
	$("#setTopBoxActivate").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "Activate Setop Box",
		buttons : {
			'OK' : function() {
				$.post("activateSetTopBox/" + $('#activateCustomerId').val(), {
					customerSetTopBoxId : $('#activateCustomerSetTopBoxId').val(),
					customerId: $("#activateCustomerId").val(),
					id: $("#activateCid").val(),
					date: $("#activateDate").val(),
					reason : $("#activateReason").val()
				}).done(function(data) {
					$("#setTopBoxActivate").dialog('close');
					$(setTopBoxGridId).trigger( 'reloadGrid' );
				});
				// Now you have the value of the textbox, you can do something
				// with it, maybe an AJAX call to your server!
			},
			'Close' : function() {
				$(this).dialog('close');
			}
		}
	});
	
	$("#setTopBoxDeactivate").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "Deactivate Setop Box",
		buttons : {
			'OK' : function() {
				$.post("deActivateSetTopBox/" + $('#deactivateCustomerId').val(), {
					customerSetTopBoxId : $('#deactivateCustomerSetTopBoxId').val(),
					customerId: $("#deactivateCustomerId").val(),
					id: $("#deactivateCid").val(),
					date: $("#deactivateDate").val(),
					reason : $("#deactivateReason").val()
				}).done(function(data) {
					$("#setTopBoxDeactivate").dialog('close');
					$(setTopBoxGridId).trigger( 'reloadGrid' );
				});
				// Now you have the value of the textbox, you can do something
				// with it, maybe an AJAX call to your server!
			},
			'Close' : function() {
				$(this).dialog('close');
			}
		}
	});
	
	$("#myAdditionalDiscountDialog").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		title : "additionalDiscount",
		buttons : {
			'OK' : function() {
				$.post("addAdditionalDiscount/" + $('#adCustomerId').val(), {
					id : $('#adCustomerSetTopBoxId').val(),
					customerId: $("#adCustomerId").val(),
					reason : $("#adReason").val(),
					creditDebit : $("#creditDebit").val(),
					amount: $("#additionalDiscount").val()
				}).done(function(data) {
					$("#myAdditionalDiscountDialog").dialog('close');
				});
				// Now you have the value of the textbox, you can do something
				// with it, maybe an AJAX call to your server!
			},
			'Close' : function() {
				$(this).dialog('close');
			}
		}
	});
	
	$("#setTopBoxReplace").dialog({
		autoOpen : false,
		modal : true,
		position: 'center',
		width: 500,
		title : "additionalDiscount",
		buttons : {
			'OK' : function() {
				$.post("addAdditionalDiscount/" + $('#adCustomerId').val(), {
					id : $('#adCustomerSetTopBoxId').val(),
					customerId: $("#adCustomerId").val(),
					reason : $("#adReason").val(),
					creditDebit : $("#creditDebit").val(),
					amount: $("#additionalDiscount").val()
				}).done(function(data) {
					$("#myAdditionalDiscountDialog").dialog('close');
				});
				// Now you have the value of the textbox, you can do something
				// with it, maybe an AJAX call to your server!
			},
			'Close' : function() {
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
	function getAllSetTopBoxes() {
		$.ajax({
		    url: '/getAllSetTopBoxes',
		    type: 'get',
		    async: false,
		    success: function( data, textStatus, jQxhr ){
    		    $("#replacedSetTopBox").find('option').remove().end().append('<option>Select Replaced Box</option>');
    		    $("#replacedSetTopBox").addClass("ui-widget ui-jqdialog");
    			$("#replacedSetTopBox").select2();
     		    for (var key in data) {
     		    	console.info(key + " ---- "+data[key]);
     		    	$("#replacedSetTopBox").append('<option value="'+key+'">'+data[key]+'</option>')
     		    }
		    },
		    error: function( jqXhr, textStatus, errorThrown ){
		    	alert("Something Went Wrong");
		    }
		});
	}
});
