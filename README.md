# idea-felix-scr-plugin
IntelliJ IDEA plugin for augment  service @reference with bind/unbind methods 

This is usefull when you writing unit tests for your OSGI component.

# Demo
 
 Lets look at simple demo service:
 ````java
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;

@Service(Handler.clsss)
@Component(name = "UserHanlder", immediate = true)
public class UserHandler {
    
    @Reference
    private UserSertvice userService;
    
    @Reference
    private OrganizationService organizationService;


    @Reference(
            bind = "bindRoles", unbind = "unbindRoles"
    )
    private RoleService roles;
    
    ....
    
    protected void bindRoles(RolesService servive) {
        ....
    }
    
    protected void unbindRoles(RolesService servive) {
            ....
    }
}
````

"stubs" will ge used for userService and organizatioService.
But not for roles (because of bindRoles method exists)

So, when you write your unit-test you can use autocomlete and "navigate-to" features:

![tag alt](https://raw.githubusercontent.com/vfpfafrf/idea-felix-scr-plugin/images/doc/demo.png)
