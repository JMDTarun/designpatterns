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
				forceFit: true,
		        autowidth: true,
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
			params.url = 'pack/' + postdata.id;
		}
	};
	var addOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'pack';
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'pack/' + postdata;
		}
	};

	var networksData;
	var URL = '/allPacks';
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
				name:'price',
				label: 'Price',
				index: 'price',
				editable: true,
				formatter:'number',
				editrules: {required: true}
			},
		],
		caption: "Packs",
		pager : '#pagerPacks',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		},
		multiselect: false,
		subGrid: true,
		caption: "Grid as Subgrid",
		subGridRowExpanded: function(subgrid_id, row_id) {
			// we pass two parameters
			// subgrid_id is a id of the div tag created whitin a table data
			// the id of this elemenet is a combination of the "sg_" + id of the row
			// the row_id is the id of the row
			// If we wan to pass additinal parameters to the url we can use
			// a method getRowData(row_id) - which returns associative array in type name-value
			// here we can easy construct the flowing
			var subgrid_table_id, pager_id;
			subgrid_table_id = subgrid_id+"_t";
			pager_id = "p_"+subgrid_table_id;
			$("#"+subgrid_id).html("<table id='"+subgrid_table_id+"' class='scroll'></table><div id='"+pager_id+"' class='scroll'></div>");
			jQuery("#"+subgrid_table_id).jqGrid({
				url:"allPackNetworkChannels/"+row_id,
				editurl: 'updatePackChannel/' + row_id,
				colNames: ['Id','Network', "Channel Class", "Network Channel", "Monthly Rent", "GST"],
				colModel: [
					{
						name:"id",
						index:"id",
						formatter:'integer',
						editable: true,
						editoptions: 
						{disabled: true, size:5}
					},
					{
						name:"network_id",
						label: 'Network',
						index:"network_id", 
						editable: true,
						editrules: {required: true},
						edittype:"select",
						formatter: function myformatter ( cellvalue, options, rowObject ) {
							return rowObject.name;
						},
				        editoptions:{
		                    dataUrl: "/getAllNetworkChannels", 
		                           buildSelect: function(jsonOrderArray) {
		                                   var s = '<select><option value="0">All</option>';
		                                   console.info(jsonOrderArray);
		                                   if (jsonOrderArray && jsonOrderArray.length) {
		                                	   var myObj = JSON.parse(jsonOrderArray);
		                                	   networksData = myObj;
		                                	   for (var key in myObj) {
		                                		    console.log(key + ': ' + myObj[key]);
		                                		    s += '<option value="'+myObj[key].network.id+'">'+myObj[key].network.name+'</option>';
		                                		}
		                                  }
		                                  return s + "</select>";
		                          }
		                   }
					},
					{
						name:"channel_id",
						index:"channel.",
						label: 'Network',
						index:"network_id", 
						editable: true,
						editrules: {required: true},
						edittype:"select",
						editoptions: {  
		                    value: "0:Select"  
		                }
					},
					{
						name:"networkChannel_id",
						label: 'Network Channel',
						index:"networkChannel_id", 
						editable: true,
						editrules: {required: true},
						edittype:"select",
						editoptions: {  
		                    value: "0:Select"  
		                }
					},
					{
						name:"monthlyRent",
						index:"Monthly Rent",
						formatter:'monthlyRent',
						editable: true,
						editoptions: 
						{disabled: true, size:5}
					},
					{
						name:"gst",
						index:"GST",
						formatter:'gst',
						editable: true,
						editoptions: 
						{disabled: true, size:5}
					}
				],
			   	rowNum:10,
			   	pager: pager_id,
			   	sortname: 'name',
			    sortorder: "asc",
			    forceFit: true,
		        autowidth: true,
			    height: '100%'
			});
		
			var editOptionsSG = {
					onclickSubmit: function(params, postdata) {
						params.url = 'updatePackNetworkChannel/' + row_id;
					}
				};
			var addOptionsSG = {
				onclickSubmit: function(params, postdata) {
					params.url = 'createPackNetworkChannel/' + row_id;
				},
				afterShowForm: populateNetworkChannels,
				mtype: "POST"
			};
			var delOptionsSG = {
				onclickSubmit: function(params, postdata) {
					params.url = 'pack/' + postdata;
				}
			};
			
			jQuery("#"+subgrid_table_id).jqGrid('navGrid',"#"+pager_id,
					{edit:false,add:true,del:true}, 
					{},
					addOptionsSG,
					delOptionsSG
				);
		},
		subGridRowColapsed: function(subgrid_id, row_id) {
			// this function is called before removing the data
			var subgrid_table_id;
			subgrid_table_id = subgrid_id+"_t";
			jQuery("#"+subgrid_table_id).remove();
		}
	};

	function populateNetworkChannels() {
		
		setTimeout(function() { 
	        // then hook the change event of the country dropdown so that it updates cities all the time  
			removeDuplicateOptions("#network_id");
			removeDuplicateOptions("#channel_id");
			
			populateConditionally();
			$("#network_id").bind("change", function (e) {
				$('#networkChannel_id').empty().append('<option value="0">Select</option>');
				$('#channel_id').empty().append('<option value="0">Select</option>');
				populateConditionally();
				removeDuplicateOptions("#networkChannel_id");
	        });
			$("#channel_id").bind("change", function (e) {
				$('#networkChannel_id').empty().append('<option value="0">Select</option>');
				populateConditionally();
				removeDuplicateOptions("#networkChannel_id");
	        });
			$("#networkChannel_id").bind("change", function (e) {
				$('#monthlyRent').val(networksData[$("#networkChannel_id").val()].monthlyRent);
				$('#gst').val(networksData[$("#networkChannel_id").val()].gst);
	        });
		}, 100);
    }  
	
	function removeDuplicateOptions(selectId) {
		var seen = {};
		jQuery(selectId).children().each(function() {
		    var txt = jQuery(this).attr('value');
		    if (seen[txt]) {
		        jQuery(this).remove();
		    } else {
		        seen[txt] = true;
		    }
		});
	}
	
	function populateConditionally() {
		for (var key in networksData) {
			if($("#network_id").val() > 0 && $('#channel_id').val() > 0 && networksData[key].network.id == $("#network_id").val() && $('#channel_id').val() == networksData[key].channel.id) {
    		    $("#networkChannel_id").append('<option value="'+networksData[key].id+'">'+networksData[key].name+'</option>');
    		} else if($("#network_id").val() > 0 && $('#channel_id').val() == 0 && networksData[key].network.id == $("#network_id").val()) {
    			$("#channel_id").append('<option value="'+networksData[key].channel.id+'">'+networksData[key].channel.name+'</option>');
    		    $("#networkChannel_id").append('<option value="'+networksData[key].id+'">'+networksData[key].name+'</option>');
    		} else if($("#network_id").val() == 0 && $('#channel_id').val() == 0) {
    			$("#networkChannel_id").append('<option value="'+key+'">'+networksData[key].name+'</option>');
    		}
		}
	}
	
	$("#packs")
			.jqGrid(options)
			.navGrid('#pagerPacks',
			{}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#packs").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });

});
