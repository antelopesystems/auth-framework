package com.antelopesystem.authframework.token.exception

import com.antelopesystem.authframework.authentication.filter.RequestFailedException


class InvalidTokenException: RequestFailedException("Invalid authentication token")