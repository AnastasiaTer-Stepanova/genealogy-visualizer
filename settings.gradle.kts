rootProject.name = "genealogy-visualizer"

include("ui")
include(":back:api")
include(":back:controller")
include(":back:dao")
include(":back:service")
include(":back:watcher")

project(":back:api").projectDir = file("back/api")
project(":back:controller").projectDir = file("back/controller")
project(":back:dao").projectDir = file("back/dao")
project(":back:service").projectDir = file("back/service")
project(":back:watcher").projectDir = file("back/watcher")
