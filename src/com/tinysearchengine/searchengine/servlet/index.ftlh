<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	<link rel="stylesheet" href="/css/common.css" />
	<link rel="stylesheet" href="/css/index.css" />
	<title>Tiny Search Engine</title>
</head>
<body class="container">
	<div id="index-container" class="col-lg-offset-3 col-lg-6">
		<h1 id="title" class="lead">TinySearchEngine</h1>
		<div id="search-form-container">
			<form id="search-form" method="post" action="/search">
				<div class="form-group">
					<input type="text" name="query" placeholder="Search here..." required>
					<button class="btn btn-primary" type="submit">Search</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>
