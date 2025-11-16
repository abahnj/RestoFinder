package com.wolt.restofinder.presentation.venues.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wolt.restofinder.R
import com.wolt.restofinder.presentation.theme.GreetingSecondary
import com.wolt.restofinder.presentation.theme.RestoFinderTheme

/**
 * Top app bar for the venue list screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueListTopAppBar(
    userName: String,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                // Left side: Profile + Greeting
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    // Profile picture placeholder
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Greeting text with two-tone styling
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = GreetingSecondary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            ) {
                                append("Let's eat, ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(userName)
                            }
                        },
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                // Right side: Search + Cart icons
                Row {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = "Search",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(onClick = onCartClick) {
                        Icon(
                            painter = painterResource(R.drawable.shopping_cart),
                            contentDescription = "Cart",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
    )
}

// MARK: - Previews

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Venue List Top App Bar", showBackground = true)
@Composable
private fun VenueListTopAppBarPreview() {
    RestoFinderTheme {
        VenueListTopAppBar(
            userName = "Abah",
            onSearchClick = {},
            onCartClick = {},
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        )
    }
}
