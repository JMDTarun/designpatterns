$(function() {

	$("#successDiv").hide();
	$("#errorDiv").hide();
	
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
			    reloadAfterSubmit:true,
				ajaxEditOptions: { contentType: "application/x-www-form-urlencoded" },
				mtype: 'POST',
				serializeEditData: function(data) {
					var url = Object.keys(data).map(function(k) {
					    return encodeURIComponent(k) + '=' + encodeURIComponent(data[k].replace("_empty", ""))
					}).join('&');
					return url;
					//encodeURIComponent("id") + '=' + encodeURIComponent(data.networkChannel_id);
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
			params.url = 'customer/' + postdata.id;
		}
	};
	
	var addOptions = {
			width: 700,
		onclickSubmit: function(params, postdata) {
			params.url = 'customer';
		},
		afterShowForm: function (formid) {
			getCustomerCode();
        },
        afterSubmit: function (response, postdata) {
			setTimeout(function() { 
				$("#add_customers").click();
			}, 100);
			return [true, '', response.responseText];
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
        rowNum: 20,
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
				hidden: true, 
				editrules: {required: true, edithidden: true},
				hidedlg: true
			},
			{
				name:'city',
				label: 'City',
				index: 'city',
				editable: true,
				hidden: true, 
				editrules: {required: true, edithidden: true},
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
				hidden: true, 
				editrules: {required: true, edithidden: true},
			},			
			{
				name:'area.id',
				label: 'Area',
				index: 'area.id',
				editable: true,
				edittype:"select",
				formatter: function myformatter ( cellvalue, options, rowObject ) {
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
					if(rowObject.area) {
						return rowObject.subArea.wardNumber;
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
					if(rowObject.area) {
						return rowObject.street.streetNumber;
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
			$("#pagerCustomers").css("filter","blur(10px)");
			$("#"+subgrid_id).html("<table id='"+subgrid_table_id+"' class='scroll'></table><div id='"+pager_id+"' class='scroll'></div>");
			jQuery("#"+subgrid_table_id).jqGrid({
				url:"allCustomerSetTopBoxes/"+row_id,
				editurl: 'updateCustomerSetTopBox/' + row_id,
				colNames: ['Id', 'Set Top box Id', 'Activate Date', 'Deactivate Date', 'Set Top Box', "Pack", "Pack Price", "Box Price", "Payment Mode", "Entry Date", "Payment Start Date", "Billing Cycle", "Opening Balance", "Discount", "Discount Frequency", "Active"],
				colModel: [
					{
						name:"id",
						index:"id",
						formatter:'integer',
						editable: true,
						hidden: true, 
						editrules: { edithidden: false }
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
						name:"setTopBoxPrice",
						label: 'Box Price',
						index:"setTopBoxPrice",
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
                gridComplete: function()
                {
                    var rows = $("#"+subgrid_table_id).getDataIDs(); 
                    for (var i = 0; i < rows.length; i++)
                    {
                    	console.log(rows[i]);
                        var status = $("#"+subgrid_table_id).getCell(rows[i],"active");
                        console.log(status);
                        if(status == 0) {
                            $("#"+subgrid_table_id).jqGrid('setRowData',rows[i],false, {  color:'white',weightfont:'bold',background:'red'});            
                        }
                    }
                },
                beforeSelectRow: function (rowid, e) {
		        	setTopBoxSelectedRowId = rowid;
		        	var myGrid = $("#"+subgrid_table_id);
		        	setTopBoxGridId = myGrid;
		        	isActiveCell = myGrid.jqGrid ('getCell', rowid, 'active');
		        	if(isActiveCell == 1) {
		        		$("#activateDeactivateButton").attr("title", "Deactivate");
		        		$('#activateDeactivateButton').children(":first").contents().filter(function() {
		        		    return this.nodeType == 3
		        		}).each(function(){
		        		    this.textContent = this.textContent.replace('Activate/Deactivate','Deactivate');
		        		    this.textContent = this.textContent.replace('Activate','Deactivate');
		        		});
		        	} else {
		        		$("#activateDeactivateButton").attr("title", "Activate");
		        		$('#activateDeactivateButton').children(":first").contents().filter(function() {
		        		    return this.nodeType == 3
		        		}).each(function(){
		        		    this.textContent = this.textContent.replace('Activate/Deactivate','Activate');
		        		    this.textContent = this.textContent.replace('Deactivate','Activate');
		        		});
		        	}
		            return true;
		        },
                subGridRowExpanded: function(subgrid_id3, row_id3) {
                    var subgrid_table_id3, pager_id3;
                    subgrid_table_id3 = subgrid_id3+"_t";
                    pager_id3 = "p_"+subgrid_table_id3;
        			$("#"+pager_id).css("filter","blur(10px)");
        			$("#"+subgrid_id3).html("<table id='"+subgrid_table_id3+"' class='scroll'></table><div id='"+pager_id3+"' class='scroll'></div>");
                    jQuery("#"+subgrid_table_id3).jqGrid({
                        url:"allCustomerSetTopBoxChannels/"+row_id3,
                        colNames: ['Id', "Network","Channel", "Network Channel", "Rent", "Entry Date", "Payment Start Date", "Deleted"],
                        colModel: [
                        	{
        						name:"id",
        						index:"id",
        						formatter:'integer',
        						editable: true,
        						hidden: true, 
        						editrules: { edithidden: false }
        					},
        					{
        						name:"network.id",
        						label: 'Network',
        						index:"network.id", 
        						hidden: true, 
        						editable: true, 
        						editrules: { edithidden: true}, 
        						hidedlg: true,
        						edittype:"select",
        						formatter: function myformatter ( cellvalue, options, rowObject ) {
        							return rowObject.name;
        						},
        				        editoptions:{
        		                    dataUrl: "/getAllNetworks", 
        		                           buildSelect: function(jsonOrderArray) {
        		                                   var s = '<select><option value="">Select Network</option>';
        		                                   if (jsonOrderArray && jsonOrderArray.length) {
        		                                	   var myObj = JSON.parse(jsonOrderArray);
        		                                	   networksData = myObj;
        		                                	   for (var key in myObj) {
        		                                		    s += '<option value="'+myObj[key].id+'">'+myObj[key].name+'</option>';
        		                                		}
        		                                  }
        		                                  return s + "</select>";
        		                          }
        		                   }
        					},
        					{
        						name:"channel.id",
        						label: 'Channel',
        						index:"channel.id", 
        						hidden: true, 
        						editable: true, 
        						editrules: { edithidden: true}, 
        						hidedlg: true,
        						edittype:"select",
        						formatter: function myformatter ( cellvalue, options, rowObject ) {
        							return rowObject.name;
        						},
        				        editoptions:{
        		                    dataUrl: "/getAllChannels", 
        		                           buildSelect: function(jsonOrderArray) {
        		                                   var s = '<select><option value="">Select Channel</option>';
        		                                   if (jsonOrderArray && jsonOrderArray.length) {
        		                                	   var myObj = JSON.parse(jsonOrderArray);
        		                                	   networksData = myObj;
        		                                	   for (var key in myObj) {
        		                                		    s += '<option value="'+myObj[key].id+'">'+myObj[key].name+'</option>';
        		                                		}
        		                                  }
        		                                  return s + "</select>";
        		                          }
        		                   }
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
        		                                   s += '<option value="">Select Network Channel</option>';
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
        						name:"total",
        						label: 'Rent',
        						index:"total",
        						formatter: function myformatter ( cellvalue, options, rowObject ) {
        							if(rowObject.networkChannel) {
        								return rowObject.networkChannel.total;
        							}
        							return 0.0;
        						},
        						editable: false,
        						hidden: false, 
        						editrules: { edithidden: true }, 
        						hidedlg: false
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
        				afterShowForm: function (formid) {
    						manageNetworkChannels();
    						populateNetworkChannels();
    	                },
        				mtype: "POST",
        				closeAfterAdd: true,
        		        closeAfterEdit: true,
        		        reloadAfterSubmit:true,
        				width: 400
        			};
        			var delOptionsSG3 = {
        				onclickSubmit: function(params, postdata) {
        					params.url = 'pack/' + postdata;
        				}
        			};
        			
        			jQuery("#"+subgrid_table_id3).jqGrid('navGrid',"#"+pager_id3,
        					{reloadAfterSubmit:true,closeAfterAdd: true,closeAfterEdit: true, edit:false,add:true,del:false, search:false, addtext: 'Add', edittext: 'Edit',deltext: 'Delete'}, 
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
                }, 
                subGridRowColapsed: function(subgrid_id, row_id) {
                	$("#"+pager_id).css("filter","blur(0px)");
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
	                closeAfterAdd: true,
	                closeAfterEdit: true,
	                reloadAfterSubmit:true,
	                width: 700
				};
			var addOptionsSG = {
					width: 700,
				onclickSubmit: function(params, postdata) {
					params.url = 'createCustomerSetTopBox/' + row_id;
				},
				afterShowForm: function (formid) {
					manageFieldsForAdd();
                },
                closeAfterAdd: true,
                closeAfterEdit: true,
                reloadAfterSubmit:true,
				mtype: "POST",
				width: 700
			};
			var delOptionsSG = {
				onclickSubmit: function(params, postdata) {
					params.url = 'pack/' + postdata;
				}
			};
			
			$("#"+subgrid_table_id).jqGrid('navGrid',"#"+pager_id,
					{reloadAfterSubmit:true,closeAfterAdd: true,closeAfterEdit: true, edit:true,add:true,del:false, search:false, addtext: 'Add', edittext: 'Edit'}, 
					editOptionsSG,
					addOptionsSG,
					delOptionsSG
				);
			$("#"+pager_id).css({"height":"55"});
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
	                    title: "Activate/Deactivate",
	                    id: "activateDeactivateButton",
	                    caption: "Activate/Deactivate",
	                    position: "last",
	                    onClickButton: function() {
	                    	var myGrid = $("#"+subgrid_table_id);
	                    	selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
	                    	setTopBoxGridId = myGrid;
	                    	if(selectedRowId) {
	                    		idCellValue = myGrid.jqGrid ('getCell', selectedRowId, 'id');
	                    		isActiveCell = myGrid.jqGrid ('getCell', selectedRowId, 'active');
	                    		if(isActiveCell == 1) {
	                    			$("#deactivateCustomerId").val(row_id);
	                    			$("#deactivateCustomerSetTopBoxId").val(idCellValue);
	                    			$("#deactivateDate").datepicker({ defaultDate: new Date() });
	                    			$("#setTopBoxDeactivate").dialog('open');
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
	                    title: "Replace",
	                    caption: "Replace",
	                    position: "last",
	                    onClickButton: function() {
	                    	var myGrid = $("#"+subgrid_table_id);
	                    	selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
	                    	setTopBoxGridId = myGrid;
	                    	if(selectedRowId) {
	                    		$("#replaceCustomerId").val(row_id);
	                    		$("#replaceCustomerCustomerSetBoxId").val(myGrid.jqGrid ('getCell', selectedRowId, 'id'));
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
			$("#"+subgrid_table_id).navButtonAdd("#"+pager_id,
	                {
	                    buttonicon: "ui-icon-trash",
	                    title: "Retrack",
	                    caption: "Retrack",
	                    position: "last",
	                    onClickButton: function() {
	                    	var myGrid = $("#"+subgrid_table_id);
	                    	selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
	                    	setTopBoxGridId = myGrid;
	                    	if(selectedRowId) {
	                    		$.ajax({url: "retrackSetTopBox?customerSetTopBoxId="+selectedRowId, success: function(result){
	                    			$("#successDiv").text("Successfully retracked Set top box.");
	                    			$("#successDiv").show();
	                    			setTimeout(function() {
	                    		        $("#successDiv").hide('blind', {}, 500)
	                    		    }, 5000);
	                    			$("#errorDiv").hide();
	                    		}});
	                    	} else {
	                    		$("#mySelectRowDialog").dialog('open');
	                    	}
	                    }
	                });
		},
		subGridRowColapsed: function(subgrid_id, row_id) {
			// this function is called before removing the data
			$("#pagerCustomers").css("filter","blur(0px)");
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
				$("#packPrice").val(packs[packSelected].total);
			}
			$("#pack\\.id").bind("change", function (e) {
				$("#packPrice").val(packs[$(this).val()].total);
			});
			$("#paymentMode").bind("change", function (e) {
				billingCycleState();
			});
		}, 100);
    }
	
	function manageFieldsForEdit() {
		setTimeout(function() {
			var myGrid = $(setTopBoxGridId);
			selectedRowId = myGrid.jqGrid ('getGridParam', 'selrow');
			$("#editmodcustomers").attr("width", "50%");
			$("#paymentMode").attr("disabled","disabled");
			$("#paymentMode").attr("readonly","readonly");
			$("#paymentMode").val(myGrid.jqGrid ('getCell', selectedRowId, 'paymentMode'));
			$('#setTopBox\\.id').find('option').remove().end().append('<option value="'+myGrid.jqGrid ('getCell', selectedRowId, 'setTopBoxId')+'">'+myGrid.jqGrid ('getCell', selectedRowId, 'setTopBox.id')+'</option>');
			//$("#setTopBox\\.id").val(myGrid.jqGrid ('getCell', selectedRowId, 'setTopBox.id'));
			$("#setTopBox\\.id").attr("disabled","disabled");
			$("#setTopBox\\.id").attr("readonly","readonly");

			addSelect2();
			billingCycleState();
			
			$("#pack\\.id").bind("change", function (e) {
				$("#packPrice").val(packs[$(this).val()].total);
			});
			$("#paymentMode").bind("change", function (e) {
				billingCycleState();
			});
		}, 100);
    }

	function getCustomerCode() {
		setTimeout(function() { 
			$.ajax({
			    url: "getCustomerCode",
			    type: 'get',
			    async: false,
			    success: function( data, textStatus, jQxhr) {
	    		    $("#customerCode").val(parseInt(data));
			    },
			    error: function( jqXhr, textStatus, errorThrown ){
			    	alert("Something Went Wrong");
			    }
			});
		}, 100);
		
	}
	
	function populateNetworkChannels() {
		
		setTimeout(function() { 
			$("#network\\.id").addClass("ui-widget ui-jqdialog");
 			$("#network\\.id").select2();
 			
			$("#channel\\.id").addClass("ui-widget ui-jqdialog");
 			$("#channel\\.id").select2();
 			
 			$("#networkChannelId").addClass("ui-widget ui-jqdialog");
			$("#networkChannelId").select2();
			
			$("#network\\.id").bind("change", function (e) {
				var channelUrlStr = "getAllChannelsByNetwork/" + $("#network\\.id").val();
				var networkChannelUrlStr = "getAllNetworkChannelsByNetworkId/"+$(this).val();
				if($(this).val() == "") {
					channelUrlStr = "getAllChannels";
					networkChannelUrlStr = "getAllNetworkChannels";
				}
				$.ajax({
				    url: channelUrlStr,
				    type: 'get',
				    async: false,
				    success: function( data, textStatus, jQxhr) {
		    		    $("#channel\\.id").find('option').remove().end().append('<option>Select Channel</option>');
		    		    $("#channel\\.id").addClass("ui-widget ui-jqdialog");
		    			$("#channel\\.id").select2();
		     		    for (var key in data) {
		     		    	$("#channel\\.id").append('<option value="'+data[key].id+'">'+data[key].name+'</option>')
		     		    }
				    },
				    error: function( jqXhr, textStatus, errorThrown ){
				    	alert("Something Went Wrong");
				    }
				});
				
				$.ajax({
				    url: networkChannelUrlStr,
				    type: 'get',
				    async: false,
				    success: function( data, textStatus, jQxhr) {
				    	$("#networkChannelId").find('option').remove().end().append('<option value="">Select Network Channel</option>');
		    		    $("#networkChannelId").addClass("ui-widget ui-jqdialog");
		    			$("#networkChannelId").select2();
		     		    for (var key in data) {
		     		    	$("#networkChannelId").append('<option value="'+data[key].id+'">'+data[key].name+'</option>')
		     		    }
				    },
				    error: function( jqXhr, textStatus, errorThrown ){
				    	alert("Something Went Wrong");
				    }
				});
	        });
			
			$("#channel\\.id").bind("change", function (e) {
				var urlStr = "";
				if($(this).val() != "" && $(this).val() != "Select Channel" && $("#network\\.id").val() != "") {
					urlStr = "getAllNetworkChannelsByChanellAndNeworkId/"+ $(this).val() +'/'+ $("#network\\.id").val();
				} else if($(this).val() != "" && $(this).val() != "Select Channel" && $("#network\\.id").val() == "") {
					urlStr = "getAllNetworkChannelsByChanellId/"+ $(this).val();
				} else if(($(this).val() == "" || $(this).val() == "Select Channel") && $("#network\\.id").val() != "") {
					urlStr = "getAllNetworkChannelsByNetworkId/" + $("#network\\.id").val();
				} else {
					urlStr = "getAllNetworkChannels";
				}
				
				$.ajax({
				    url: urlStr,
				    type: 'get',
				    async: false,
				    success: function( data, textStatus, jQxhr) {
		    		    $("#networkChannelId").find('option').remove().end().append('<option value="">Select Network Channel</option>');
		    		    $("#networkChannelId").addClass("ui-widget ui-jqdialog");
		    			$("#networkChannelId").select2();
		     		    for (var key in data) {
		     		    	$("#networkChannelId").append('<option value="'+data[key].id+'">'+data[key].name+'</option>')
		     		    }
				    },
				    error: function( jqXhr, textStatus, errorThrown ){
				    	alert("Something Went Wrong");
				    }
				});
	        });
		}, 100);
    }  
	
	function addSelect2() {
		$("#pack\\.id").addClass("ui-widget ui-jqdialog");
		$("#pack\\.id").select2();
		
		$("#setTopBox\\.id").addClass("ui-widget ui-jqdialog");
		$("#setTopBox\\.id").select2();
	}
	
	function manageNetworkChannels() {
		setTimeout(function() { 
			
		}, 100);
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
					$("#myDialog").dialog('close');
					$("#myDialog").dialog('refresh');
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
					$("#myAdditionalDiscountDialog").dialog('refresh');
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
					$("#myAdditionalChargeDialog").dialog('refresh');
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
					$('#channelRemoveDialog').dialog("refresh");
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
					$('#setTopBoxActivate').dialog("refresh");
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
					$("#setTopBoxDeactivate").dialog('refresh');
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
					$("#myAdditionalDiscountDialog").dialog('refresh');
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
		width: 700,
		title : "replaceSetTopBox",
		buttons : {
			'OK' : function() {
				$.post("replaceSetTopBox/" + $('#replaceCustomerId').val(), {
					id : 0,
					customerId: $("#replaceCustomerId").val(),
					"oldSetTopBox.id": $("#currentSetTopBox").val(),
					"replacedSetTopBox.id": $("#replacedSetTopBox").val(),
					replacementReason : $("#replacementReason").val(),
					replacementType : $("#replacementType").val(),
					replacementCharge: $("#replacementCharge").val(),
					customerSetTopBoxId: $("#replaceCustomerCustomerSetBoxId").val()
				}).done(function(data) {
					$("#setTopBoxReplace").dialog('close');
					$("#setTopBoxReplace").dialog('refresh');
					$(setTopBoxGridId).trigger( 'reloadGrid' );
				});
				// Now you have the value of the textbox, you can do something
				// with it, maybe an AJAX call to your server!
			},
			'Close' : function() {
				$("#setTopBoxReplace").dialog('close');
			}
		}
	});

	$("ui-jqgrid-pager").css({"height":"55"});
	
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
     		    	$("#replacedSetTopBox").append('<option value="'+key+'">'+data[key]+'</option>')
     		    }
		    },
		    error: function( jqXhr, textStatus, errorThrown ){
		    	alert("Something Went Wrong");
		    }
		});
	}
});
