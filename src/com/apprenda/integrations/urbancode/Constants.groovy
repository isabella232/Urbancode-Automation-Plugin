package com.apprenda.integrations.urbancode

public class Constants {
	final static def REST_API_PATHS = [
		Auth:'/authentication/api/v1/sessions/developer', 
		NewVersion:'/developer/api/v1/versions/', 
		GetAliases:'/developer/api/v1/apps/', 
		GetVersions:'/developer/api/v1/versions/', 
		PromoteDemote:'/developer/api/v1/versions/',
		NewApplication: '/developer/api/v1/apps',
		DeleteApplication: '/developer/api/v1/apps',
		GetAddonInstances: '/developer/api/v1/addons',
		SetInstanceCount: '/developer/api/v1/components',
		GetComponentInfo: '/developer/api/v1/components'
]}
