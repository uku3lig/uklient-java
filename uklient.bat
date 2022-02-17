set argC=0
for %%x in (%*) do Set /A argC+=1

IF %argC% > 0 (
    gradlew -q --console plain run --args="%*"
) ELSE (
    gradlew -q --console plain run
)