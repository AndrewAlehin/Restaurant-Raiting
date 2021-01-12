## API documentation:
`http://localhost:8080/swagger-ui/index.html`

### curl samples (application deployed at application context `restaurantraiting`).
> For windows use `Git Bash`

#### get All Users
`curl -s http://localhost:8080/rest/admin/users --user admin@gmail.com:admin`

#### get Users 1
`curl -s http://localhost:8080/rest/admin/users/1 --user admin@gmail.com:admin`

#### get All Restaurants
`curl -s http://localhost:8080/rest/profile/restaurants --user user@yandex.ru:password`

#### increase Vote Restaurant 1
`curl -s http://localhost:8080/rest/profile/increase/1 --user user@yandex.ru:password`

#### get Restaurant 1
`curl -s http://localhost:8080/rest/admin/restaurants/1  --user admin@gmail.com:admin`

#### get Restaurant not found
`curl -s -v http://localhost:8080/rest/admin/restaurants/8 --user admin@gmail.com:admin`

#### delete Restaurant
`curl -s -X DELETE http://localhost:8080/rest/admin/restaurants/2 --user admin@gmail.com:admin`

#### create Restaurant
`curl -s -X POST -d '{"name":"Created Restaurant","date":"2021-02-01"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/rest/admin/restaurants --admin@gmail.com:admin`

#### update Restaurant
`curl -s -X PUT -d '{"name":"Created Restaurant","date":"2021-02-01"}' -H 'Content-Type: application/json' http://localhost:8080/rest/admin/restaurants/1 --user admin@gmail.com:admin`

#### get All Meals Restaurant 1
`curl -s http://localhost:8080/rest/admin/meals/1 --admin@gmail.com:admin`

#### get Meals 1
`curl -s http://localhost:8080/rest/admin/meals/1/1  --admin@gmail.com:admin`

#### get Meals not found
`curl -s -v http://localhost:8080/rest/admin/meals/1/8 --admin@gmail.com:admin`

#### delete Meals
`curl -s -X DELETE http://localhost:8080/rest/admin/meals/1/2 --admin@gmail.com:admin`

#### create Meals
`curl -s -X POST -d '{"name":"Created lunch","price":300}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/rest/admin/meals/1 --admin@gmail.com:admin`

#### update Meals
`curl -s -X PUT -d '{"name":"Updated breakfast", "price":200}' -H 'Content-Type: application/json' http://localhost:8080/rest/admin/meals/1/1 --admin@gmail.com:admin`