### 作业要求

#### 在demo的基础上完成下面需求：
* 重构添加热搜的接口，热搜事件现在增加一个字段:user，用来表示是哪个用户添加的该热搜事件。
  用户所包含的字段以及字段的限制和demo中保持一致，请求例子：
  ```
  {
      "eventName": "添加一条热搜",
      "keyword": "娱乐",
      "user": {
        "userName": "xiaowang",
        "age": 19,
        "gender": "female",
        "email": "a@thoughtworks.com",
        "phone": 18888888888
      }
  }
  ``` 
  
* 如果userName已存在在user列表中的话则只需添加热搜事件到热搜事件列表，如果userName不存在，则将User添加到热搜事件列表中（相当于注册用户）
* 需要对请求进行校验：其中user keyword eventName都不能为空, user的校验规则：
    ```
  名称(不超过8位字符，不能为空)
  性别（不能为空）
  年龄（18到100岁之间，不能为空）
  邮箱（符合邮箱规范）
  手机号（1开头的11位数字，不能为空）
    ```
  
* 测试需要对每个验证条件进行覆盖

notice: 注意@Valid和@Validated的配合使用



  





