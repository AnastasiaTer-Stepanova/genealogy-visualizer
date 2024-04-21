rootProject.name = "genealogy-visualizer"

include("ui")
include(":back:controller")
include(":back:adapter")
include(":back:service")


project(":back:adapter").projectDir = file("back/adapter")
project(":back:controller").projectDir = file("back/controller")
project(":back:service").projectDir = file("back/service")
