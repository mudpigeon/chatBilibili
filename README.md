# chatBilibili


## 组织结构

```
chatBilibili
├── chatBilibili-api -- 定义一些通用的枚举、实体类，定义 DO\DTO\VO 等
├── chatBilibili-core -- 核心工具/组件相关模块，如工具包 util， 通用的组件都放在这个模块（以包路径对模块功能进行拆分，如搜索、缓存、推荐等）
├── chatBilibili-service -- 服务模块，业务相关的主要逻辑，DB 的操作都在这里
├── chatBilibili-ui -- HTML 前端资源（包括 JavaScript、CSS、Thymeleaf 等）
├── chatBilibili-web -- Web模块、HTTP入口、项目启动入口，包括权限身份校验、全局异常处理等
``