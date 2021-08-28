# Add any ProGuard configurations specific to this
# extension here.

-keep public class xyz.kumaraswamy.loadz.Loadz {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'xyz/kumaraswamy/loadz/repack'
-flattenpackagehierarchy
-dontpreverify
