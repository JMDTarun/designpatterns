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
			params.url = 'networkChannel/' + postdata.id;
		}
	};
	var addOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'networkChannel';
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'networkChannel/' + postdata;
		}
	};

	var URL = '/allNetworkChannels';
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
				name:'monthlyRent',
				label: 'Monthly Rent',
				index: 'monthlyRent',
				formatter:'double',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'gst',
				label: 'GST',
				index: 'gst',
				formatter:'number',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'network.id',
				label: 'Network',
				index: 'network.id',
				editable: true,
				edittype:"select",
				formatter: function myformatter ( cellvalue, options, rowObject ) {
					console.info(rowObject);
					if(rowObject.network) {
						return rowObject.network.name;
					}
					return "";
				},
		        editoptions:{
                    dataUrl: "/getAllNetworks", 
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
				name:'channel.id',
				label: 'Channel',
				index: 'channel.id',
				formatter: function myformatter ( cellvalue, options, rowObject ) {
					console.info(rowObject);
					if(rowObject.channel) {
						return rowObject.channel.name;
					}
					return "";
				},
				editable: true,
				edittype:"select",
		        editoptions:{
                    dataUrl: "/getAllChannels", 
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
			}
		],
		caption: "Channels",
		pager : '#pagerChannelNetworks',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#channelNetworks")
			.jqGrid(options)
			.navGrid('#pagerChannelNetworks',
			{}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#channelNetworks").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });
	var myVar = '';
	function getAllChannels(){
		$.ajax({
	        type: "GET",
	        url: '/getAllChannels',
	        async: false,
	        success: function(data) {
	            // Run the code here that needs
	            //    to access the data returned
	        	myVar = data;
	        },
	        error: function() {
	            alert('Error occured');
	        }
	    });
		return myVar;
    }
	
	function getAllNetworks(){
        $.getJSON("/getAllNetworks", null, function(data) {
        	console.info(data);
            if (data != null) {
            	console.info(data);
            	return data;
            }
        });
    }
	
});
