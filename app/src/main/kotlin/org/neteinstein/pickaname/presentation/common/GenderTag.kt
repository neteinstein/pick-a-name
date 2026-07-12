package org.neteinstein.pickaname.presentation.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.neteinstein.pickaname.R
import org.neteinstein.pickaname.domain.model.Gender

/** Small colored badge indicating which [Gender] a name is approved for. */
@Composable
fun GenderTag(gender: Gender, modifier: Modifier = Modifier) {
    val containerColor = when (gender) {
        Gender.FEMALE -> MaterialTheme.colorScheme.secondaryContainer
        Gender.MALE -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val contentColor = when (gender) {
        Gender.FEMALE -> MaterialTheme.colorScheme.onSecondaryContainer
        Gender.MALE -> MaterialTheme.colorScheme.onTertiaryContainer
    }
    val label = when (gender) {
        Gender.FEMALE -> stringResource(R.string.gender_female)
        Gender.MALE -> stringResource(R.string.gender_male)
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
