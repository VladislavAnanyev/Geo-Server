<#assign
    known = Session.SPRING_SECURITY_CONTEXT??
>

<#if known>
    <#assign
        nowUser = Session.SPRING_SECURITY_CONTEXT.authentication.principal
        name = nowUser.getUsername()

    >
    <#else>
    <#assign
    name = "unknown"
    >
</#if>

