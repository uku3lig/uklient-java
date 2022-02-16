set argC=0
for %%x in (%*) do Set /A argC+=1

IF %argC% > 0 (
    gradlew --console plain run --args="%*"
) ELSE (
    gradlew --console plain run
)