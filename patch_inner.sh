#!/bin/bash
cat << 'REPLACE' > sed_inner.sed
/val list = mutableListOf<DayItem>()/,/val dStr = SimpleDateFormat/{
  s/val todayStr = SimpleDateFormat/val todayStrInner = SimpleDateFormat/
}
REPLACE
sed -i -f sed_inner.sed app/src/main/java/com/example/ui/screens/TodayScreen.kt
