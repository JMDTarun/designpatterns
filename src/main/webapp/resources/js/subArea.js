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
				sortname: 'wardNumber',
				sortorder: 'asc',
				height: '200',
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
		width: 500,
		onclickSubmit: function(params, postdata) {
			params.url = 'subArea/' + postdata.id;
		}
	};
	var addOptions = {
		width: 500,
		onclickSubmit: function(params, postdata) {
			params.url = 'subArea';
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'subArea/' + postdata;
		}
	};

	var URL = '/allSubAreas';
	var options = {
		url: URL,
		editurl: URL,
		height: '200',
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
				name:'wardNumber',
				label: 'Ward Number',
				index: 'wardNumber',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'wardNumber2',
				label: 'Ward Number 2',
				index: 'wardNumber2',
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
			}
		],
		caption: "SubAreas",
		pager : '#pagerSubAreas',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#subAreas")
			.jqGrid(options)
			.navGrid('#pagerSubAreas',
			{addtext: 'Add', search:false, edittext: 'Edit',deltext: 'Delete'}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#pagerSubAreas").css({"height":"55"});
	
	$("#subAreas").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });

});
