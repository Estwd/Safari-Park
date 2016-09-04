Safari-Park

Safari-Park are a set of libraries that make the integration of Zookeeper with
web platform in common use cases only a choice. In most cases it removes the
need to write any extra code that isn't specific to your environment - and
makes writing that code clear and precise.
In any of the edge cases I haven't covered, I have implemented the code
as simple and readable as I could and with the best coding standards - so
that expanding it should be a breeze.


Safari-Leopard:
For using Zookeeper as a dynamic configuration data base via Netflix's
Archaius.
This library removes the need to worry about how to choose and update values
in a regular distributed platform - with multiple services, instances and
development environments. No need for duplication of values and writing any
extra cumbersome code to choose them, only configuring an hierarchical tree
tailored to your platform.

Safari-Lion:
For using Zookeeper as an Apache's Shiro session data store.
An out of the box Shiro plugin tailored for all but the most crowded platforms.
Built specifically to counter Zookeeper's weakness with ZNodes containing huge
amounts of children ZNodes - using the newest features in the Zookeeper client
to make an impossible synchronization problem easy and readable.

Website: http://www.estwd.com/projects.html
