--登录
curl --location --request POST 'http://192.168.89.128/prod-api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
"username": "admin",
"password": "admin123"
}'
{"code":200,"msg":null,"data":{"access_token":"eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VyX2tleSI6Ijc2Mjc0MTVkLWU5ZWMtNGVjOS1hZjU4LTg3NmZhYjhjNjg4ZiIsInVzZXJuYW1lIjoiYWRtaW4ifQ.iVvxyolU6iDhmzQ3D_hM1fYCcqUdC1POWP2LBvpB0p31xovvz6hdN_9kSEre-xcjxjC36xn9hdu1Jm6I9f43Sw","expires_in":720}}
--添加
curl --location --request POST 'http://192.168.89.128/prod-api/system/product' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VyX2tleSI6ImMzZDVmYzhjLTM2YzUtNDM3Ny1hMDMxLWRmNzA4ZTc2YTUwZCIsInVzZXJuYW1lIjoiYWRtaW4ifQ.GoKt-YxgCE7K8fHPqo7WPCSJOTQNatQ1rkTsBonKDgH8PFkE7b65xDCuQxm-XHrBzFkZLP1enyBhx4ahjVPXgQ' \
--header 'Content-Type: application/json' \
--data-raw '{
"name": "商品1"
}'
{
"msg": "操作成功",
"code": 200
}
--修改
curl --location --request PUT 'http://192.168.89.128/prod-api/system/product' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VyX2tleSI6ImMzZDVmYzhjLTM2YzUtNDM3Ny1hMDMxLWRmNzA4ZTc2YTUwZCIsInVzZXJuYW1lIjoiYWRtaW4ifQ.GoKt-YxgCE7K8fHPqo7WPCSJOTQNatQ1rkTsBonKDgH8PFkE7b65xDCuQxm-XHrBzFkZLP1enyBhx4ahjVPXgQ' \
--header 'Content-Type: application/json' \
--data-raw '{
"createBy": "",
"createTime": "2024-04-08 15:16:11",
"updateBy": "",
"updateTime": null,
"remark": null,
"id": 3,
"name": "商品1",
"delFlag": "0"
}'
{
"msg": "操作成功",
"code": 200
}
--删除
curl --location --request DELETE 'http://192.168.89.128/prod-api/system/product/2' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VyX2tleSI6ImMzZDVmYzhjLTM2YzUtNDM3Ny1hMDMxLWRmNzA4ZTc2YTUwZCIsInVzZXJuYW1lIjoiYWRtaW4ifQ.GoKt-YxgCE7K8fHPqo7WPCSJOTQNatQ1rkTsBonKDgH8PFkE7b65xDCuQxm-XHrBzFkZLP1enyBhx4ahjVPXgQ'
{
"msg": "操作成功",
"code": 200
}