<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	<link rel="stylesheet" href="/css/common.css" />
	<link rel="stylesheet" href="/css/searchresult.css" />
	<title>Tiny Search Engine</title>
</head>
<body class="container">
	<div id="search-result-header" class="col-lg-12">
		<div id="search-form-container">
			<form id="search-form" method="post" action="/search">
				<div class="form-group">
					<input type="text" name="query" value="${(query)!}" placeholder="Search here..." required>
					<button class="btn btn-primary" type="submit">Search</button>
				</div>
			</form>
		</div>
	</div>
	
	<#if thirdPartyResults?has_content >
	<div id="thirdparty-result-container" class="col-lg-offset-3 col-lg-6">		
		<#list thirdPartyResults as resultItem>
			<div class="thirdparty-result-item">
				<img src="${resultItem.imageUrl}"/>
				<h4><a href="${resultItem.itemUrl}">${resultItem.title}</a></h4>
				<p>${resultItem.price}</p>
			</div>
		</#list>
	</div>
	</#if>
	
	<div id="search-result-container" class="col-lg-offset-3 col-lg-6">
		<#list searchResults as resultItem>
			<div class="search-result-item">
				<h3 class="search-result-item-link">
					<a href="${resultItem.url}">${resultItem.title}</a>
				</h3>
				<div class="search-result-item-body">
					<div><cite>${resultItem.url}</cite></div>
					<span>${resultItem.summary}</span>
				</div>
			</div>
		</#list> 
	</div>
</body>
</html>
