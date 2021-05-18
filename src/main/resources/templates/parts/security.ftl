

    <#assign
        known = Session.SPRING_SECURITY_CONTEXT??
<#--    knownoauth = Session.SPRING_SECURITY_CONTEXT.authentication.principal.attributes??-->
<#--    knownhttp = Session.SPRING_SECURITY_CONTEXT??-->
    >



<#if known>
    <#assign
<#--        nowUser = Session.SPRING_SECURITY_CONTEXT.authentication.principal.attributes-->
<#--        <#if nowUser??> -->
        nowUser = Session.SPRING_SECURITY_CONTEXT.authentication
<#--        name = nowUser.email?replace("@gmail.com","")-->
<#--    name = nowUser.toString()-->


    >

    <#if nowUser.principal.username??>
        <#assign
        name = nowUser.principal.username
            >

        <#elseif nowUser.authorizedClientRegistrationId = "github">
            <#assign
    <#--        name = nowUser.username-->
            name = nowUser.principal.attributes.login
            >
        <#elseif nowUser.authorizedClientRegistrationId = "google">
            <#assign
                        name = nowUser.principal.attributes.email?replace("@gmail.com","")
                >




        <#--<#elseif nowUser.login??>


            <#assign
&lt;#&ndash;            name = nowUser.attributes.email?replace("@gmail.com","")&ndash;&gt;
                name = nowUser.attributes.login
            >-->
    </#if>

    <#--<#elseif knownhttp>
    <#assign
    nowUser = Session.SPRING_SECURITY_CONTEXT.authentication.principal
    name = nowUser.username
        >-->
    <#else>
    <#assign
    name = "unknown"
    >
</#if>







<#--<#if nowUser??>
    <#assign name = nowUser.username>
</#if>-->

